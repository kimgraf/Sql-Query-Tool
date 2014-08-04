package controllers

import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.indexes.{IndexType, Index}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{LoggerFactory, Logger}
import play.api.libs.json._
import models._
import traits.DocumentControllerTrait
import models.JsonFormats.sqlQueryFormat
import javax.inject.Singleton

@Singleton
class SqlQueriesController extends DocumentControllerTrait[SqlQuery]  {
  implicit var format : Format[SqlQuery] = sqlQueryFormat

  def collection: JSONCollection = db.collection[JSONCollection]("sqlquery")
  collection.indexesManager.ensure(
    Index(List("name" -> IndexType.Ascending), unique = true)
  )


//  def createSqlQuery = Action.async(parse.json) {
//    println("in createSqlQuery")
//    request =>
//      val v = request.body.validate[SqlQuery]
//
//      if(v.isInstanceOf[JsError] ){
//        logger.error(v.toString)
//        BadRequest("invalid json " + v)
//      }
//      v.map{
//        source =>
//          collection.insert(source).map {
//            lastError =>
//              logger.error(s"Successfully inserted with LastError: $lastError")
//              Created("SQL Query Configuration Created")
//          } .recover {
//            case LastError(ok, err, code, errMsg, originalDocument, updated, updatedExisting) =>
//              logger.error("Mongo error, ok: " + ok + " err: " + err + " code: " + code + " errMsg: " + errMsg)
//              if (code.get == 11000) Conflict("SQL Query already exists " + errMsg) else InternalServerError
//          }
//      }.getOrElse(Future.successful(BadRequest("Failed to insert SQL Query" )))
//  }
//
//  def findSqlQuery(name:String) = Action.async {
//    Future.successful(BadRequest("findSqlQuery"))
//
////    val cursor: Cursor[SqlQuery] = collection.
////      // find all
////      find(Json.obj()).
//
//  }
//
//  def findSqlQueries = Action.async {
//    println("in findSqlQueries")
//    // let's do our query
//    val cursor: Cursor[SqlQuery] = collection.
//      // find all
//      find(Json.obj()).
//      // sort them by creation date
//      sort(Json.obj("name" -> 1)).
//      // perform the query and get a cursor of JsObject
//      cursor[SqlQuery]
//
//    // gather all the JsObjects in a list
//    val futureSqlQueriesList: Future[List[SqlQuery]] = cursor.collect[List]()
//
//    // transform the list into a JsArray
//    val futureSqlQueriesJsonArray: Future[JsArray] = futureSqlQueriesList.map { queries =>
//      Json.arr(queries)
//    }
//    // everything's ok! Let's reply with the array
//    futureSqlQueriesJsonArray.map {
//      queries =>
//        Ok(queries(0))
//    }
//  }

}
