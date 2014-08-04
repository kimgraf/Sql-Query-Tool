package controllers

/**
 * Created by kim on 25/07/14.
 */

import java.io.File
import java.sql.{DriverManager, Connection}

import play.api.mvc.Action
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.indexes.{IndexType, Index}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{LoggerFactory, Logger}
import play.api.libs.json._
import models._
import reactivemongo.core.commands.LastError
import traits.DocumentControllerTrait
import javax.inject.Singleton
import services.dynamicdriver.{DynamicDriverManagerFactory, DynamicDriverManager}

import scala.concurrent.Future

@Singleton
class JdbcDatabaseController extends DocumentControllerTrait[JdbcDatabaseConfig] {
  implicit var format : Format[JdbcDatabaseConfig] = Json.format[JdbcDatabaseConfig]
  implicit var jdbcDriverFormat : Format[JdbcDriverConfig] = Json.format[JdbcDriverConfig]


  def collection: JSONCollection = db.collection[JSONCollection]("jdbcdatabaseconfig")
  def driverscol: JSONCollection = db.collection[JSONCollection]("jdbcdrivers")
  collection.indexesManager.ensure(
    Index(List("name" -> IndexType.Ascending), unique = true)
  )

  def testDatabase = Action.async(parse.json) {
    println("in testDriver()")
    request =>

      val v = request.body.validate[JdbcDatabaseConfig]

      if(v.isInstanceOf[JsError] ){
        logger.error(v.isError.toString)
        BadRequest("invalid json " + v)
      }
      val database = request.body.as[JdbcDatabaseConfig]
      val query = Json.obj(("name", database.driver))

      val futureDriver = driverscol.
        find(query).
        one[JdbcDriverConfig] // <--- eeek!

      var dynamicDriver: DynamicDriverManager = null

      futureDriver.map {
        case Some(driver) => {
          val location = "jdbcdrivers/" + driver.driverFiles(0)

          try {
            dynamicDriver = DynamicDriverManagerFactory.create(
              java.util.Collections.singletonList(new File(location).toURI.toURL),
              driver.driverClass,
              "resources/chaperon.jar")
            var connection: Connection = null

            connection = DriverManager.getConnection(database.url.toString, database.userName.get, database.password.get)

//            create the statement, and run the select query
                          val statement = connection.createStatement()
                          val resultSet = statement.executeQuery("SELECT * FROM polls_poll")
                          while ( resultSet.next() ) {
                            val host = resultSet.getString("question")
                            val user = resultSet.getString("pub_date")
                            logger.error("question, pub_date = " + host + ", " + user)
                          }
            Ok(Json.toJson(driver))
          } catch {
            case e: AnyRef => {
              logger.debug(e.toString)
              BadRequest(s"Error ${e.toString}")
            }
          }
          finally {
            if (connection != null)
              connection.close()
            dynamicDriver.deregister()
          }
        }
        case None => NotFound(Json.obj("message" -> "Driver not found"))
      }


  }


}
