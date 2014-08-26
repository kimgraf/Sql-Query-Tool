package traits

import play.api.libs.json.{JsArray, Json, JsError}
import play.api.mvc.{Controller, Action}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future
import org.slf4j.{LoggerFactory, Logger}
import reactivemongo.core.commands.LastError
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.api.libs.json._
import reactivemongo.api.Cursor

/**
 * Created by kim on 28/07/14.
 */
trait DocumentControllerTrait[A] extends Controller with MongoController {

  final val logger: Logger = LoggerFactory.getLogger(this.getClass)
  def collection: JSONCollection

  implicit var format : Format[A]

  def findByName( name : String) = Action.async {
    println("in findByName")
    val query = Json.obj(("name", name))

    collection.
      find(query).
      one[A].map {
      case Some(item) => Ok(Json.toJson(item))
      case None => NotFound(Json.obj("message" -> "No such docuemnt"))
    }
  }

  def deleteByName(name : String) = Action.async {
    println(s"in deleteByName ${name}")
    val selector = Json.obj(
      "name" -> name)

    collection.remove(selector).map {
    lastError =>
      logger.error(s"Successfully deleted with LastError: $lastError")
      Ok("Document deleted")
    }.recover {
      case LastError(ok, err, code, errMsg, originalDocument, updated, updatedExisting) =>
        logger.error("Mongo error, ok: " + ok + " err: " + err + " code: " + code + " errMsg: " + errMsg)
        BadRequest("Document not deleted " + errMsg)
    }
  }

  def findAllByName(name: String) = Action.async {
    // let's do our query
    val cursor: Cursor[JsObject] = collection.
      // find all people with name `name`
      find(Json.obj("name" -> name)).
      // sort them by creation date
      sort(Json.obj("created" -> -1)).
      // perform the query and get a cursor of JsObject
      cursor[JsObject]

    // gather all the JsObjects in a list
    val futurePersonsList: Future[List[JsObject]] = cursor.collect[List]()

    // transform the list into a JsArray
    val futurePersonsJsonArray: Future[JsArray] = futurePersonsList.map { persons =>
      Json.arr(persons)
    }

    // everything's ok! Let's reply with the array
    futurePersonsJsonArray.map { persons =>
      Ok(persons)
    }
  }

  def createDocument = Action.async(parse.json) {
    println("in createDocument")
    request =>

      val v = request.body.validate

      if(v.isInstanceOf[JsError] ){
        logger.error(v.isError.toString)
        BadRequest("invalid json " + v)
      }

      v.map{
        source =>
          collection.insert(source).map {
            lastError =>
              logger.error(s"Successfully inserted with LastError: $lastError")
              Created("Document Created")
          } .recover {
            case LastError(ok, err, code, errMsg, originalDocument, updated, updatedExisting) =>
              logger.error("Mongo error, ok: " + ok + " err: " + err + " code: " + code + " errMsg: " + errMsg)
              if (code.get == 11000) Conflict("Document already exists " + errMsg) else InternalServerError
          }
      }.getOrElse(Future.successful(BadRequest("Failed to insert document" )))
  }

  def updateDocument(name: String) = Action.async(parse.json) {
    println("in createDocument")
    request =>

      val v = request.body.validate

      if(v.isInstanceOf[JsError] ){
        logger.error(v.isError.toString)
        BadRequest("invalid json " + v)
      }

      v.map{
        source =>
          collection.update(Json.obj("name" -> name),source).map {
            lastError =>
              logger.error(s"Successfully inserted with LastError: $lastError")
              Created("Document Updated")
          } .recover {
            case LastError(ok, err, code, errMsg, originalDocument, updated, updatedExisting) =>
              logger.error("Mongo error, ok: " + ok + " err: " + err + " code: " + code + " errMsg: " + errMsg)
              if (code.get == 11000) Conflict("Document already exists " + errMsg) else InternalServerError
          }
      }.getOrElse(Future.successful(BadRequest("Failed to update document" )))
  }

  def findAllDocuments = Action.async {
      // let's do our query
      val cursor: Cursor[A] = collection.
        // find all
        find(Json.obj()).
        // sort them by creation date
        sort(Json.obj("name" -> 1)).
        // perform the query and get a cursor of JsObject
        cursor[A]

      // gather all the JsObjects in a list
      val futureList: Future[List[A]] = cursor.collect[List]()

      // transform the list into a JsArray
      val futureJsonArray: Future[JsArray] = futureList.map { documents =>
        Json.arr(documents)
      }
      // everything's ok! Let's reply with the array
      futureJsonArray.map {
        documents =>
          Ok(documents(0))
      }
  }

}
