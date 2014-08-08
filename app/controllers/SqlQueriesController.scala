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
import play.api.mvc.{Controller, Action}
import scala.concurrent.Future


@Singleton
class SqlQueriesController extends DocumentControllerTrait[SqlQuery]  {
  implicit var format : Format[SqlQuery] = sqlQueryFormat

  def collection: JSONCollection = db.collection[JSONCollection]("sqlquery")
  collection.indexesManager.ensure(
    Index(List("name" -> IndexType.Ascending), unique = true)
  )

  def runQuery = Action.async(parse.json) {
    println("in runQuery")
    request =>
      val v = request.body.validate[SqlQuery]

      if(v.isInstanceOf[JsError] ){
        logger.error(v.toString)
        BadRequest("invalid json " + v)
      }

      Future.successful(Ok("runQuery"))
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
  }

}
