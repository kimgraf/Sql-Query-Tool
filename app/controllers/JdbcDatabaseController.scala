package controllers

/**
 * Created by kim on 25/07/14.
 */

import play.api.mvc.Action
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.indexes.{IndexType, Index}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{LoggerFactory, Logger}
import play.api.libs.json._
import models._
import traits.DocumentControllerTrait
import javax.inject.Singleton
import services.dynamicdriver.{DynamicDriverManagerFactory, DynamicDriverManager}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import services.jdbcquery.QueryManger

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
      var res = ""
      var err = ""
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

      var driver:JdbcDriverConfig = Await.result(futureDriver, 10 seconds ).get

      if(!driver.isInstanceOf[JdbcDriverConfig]) {
        err = "Failed to get JdbcDriverConfig"
       logger.debug(err)
      }

      if(err.length > 0)
        Future.successful(BadRequest(err))

      Future.successful(Ok(QueryManger.testConnection(driver, database)))
    }




}
