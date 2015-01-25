package controllers

import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.indexes.{IndexType, Index}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{LoggerFactory, Logger}
import play.api.libs.json._
import models._
import traits.DocumentControllerTrait
//import models.JsonFormats.sqlQueryFormat
import javax.inject.Singleton
import play.api.mvc.{Controller, Action}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import services.jdbcquery.QueryManger


@Singleton
class SqlQueriesController extends DocumentControllerTrait[SqlQuery]  {
  implicit var databaseRormat : Format[JdbcDatabaseConfig] = Json.format[JdbcDatabaseConfig]
  implicit var jdbcDriverFormat : Format[JdbcDriverConfig] = Json.format[JdbcDriverConfig]
  implicit var format : Format[SqlQuery] = Json.format[SqlQuery] //sqlQueryFormat

  def databasecol: JSONCollection = db.collection[JSONCollection]("jdbcdatabaseconfig")
  def driverscol: JSONCollection = db.collection[JSONCollection]("jdbcdrivers")
  def collection: JSONCollection = {
    val collection = db.collection[JSONCollection]("sqlquery")
    collection.indexesManager.ensure(
      Index(List("name" -> IndexType.Ascending), unique = true)
    )
    return collection
  }

  def runQuery = Action.async(parse.json) {
    println("in runQuery")
    request =>
      var res = ""
      var err = ""
      val v = request.body.validate[SqlQuery]

      if(v.isInstanceOf[JsError] ){
        logger.error(v.toString)
        BadRequest("invalid json " + v)
      }

      val sqlquery =  request.body.as[SqlQuery]

//      val database = request.body.as[JdbcDatabaseConfig]
      val dbquery = Json.obj(("name", sqlquery.database))

      val futureDatabase = databasecol.
        find(dbquery).
        one[JdbcDatabaseConfig] // <--- eeek!

      var database:JdbcDatabaseConfig = Await.result(futureDatabase, 10 seconds ).get

      if(!database.isInstanceOf[JdbcDatabaseConfig]) {
        err = "Failed to get JdbcDatabaseConfig"
        logger.debug(err)
      }

      val query = Json.obj(("name", database.driver))

      val futureDriver = driverscol.
        find(query).
        one[JdbcDriverConfig] // <--- eeek!

      var driver:JdbcDriverConfig = Await.result(futureDriver, 10 seconds ).get

      if(!driver.isInstanceOf[JdbcDriverConfig]) {
        err = "Failed to get JdbcDriverConfig"
        logger.debug(err)
      }

      val resObj = QueryManger.executeQuery(driver, database, sqlquery.queryText)

      if(resObj == null)
        Future.successful(BadRequest(QueryManger.err))
      else
        Future.successful(Ok(resObj))
  }

}
