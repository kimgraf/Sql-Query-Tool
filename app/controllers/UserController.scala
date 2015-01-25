package controllers

import org.slf4j.{LoggerFactory, Logger}
import play.api.mvc._
import models._
import models.users._
import play.api.libs.json._
import reactivemongo.api.indexes.{IndexType, Index}
import reactivemongo.core.commands.LastError


import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.{Future, Await}
import models.users.JsonFormats.userFormat

/**
 * Created by kim on 25/10/14.
 */
class UserController extends Controller {
  final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def findByName( name : String) = Action.async {
    UserDao.findOne(Json.obj("name" -> name)).map { user =>
      Ok(Json.toJson(user.get))
    }
  }

   def findAllDocuments = Action.async {
    println("in User.findAllDocuments")
//    var users = UserDao.find(page = 1, pageSize = 10)
    var users = UserDao.findAll()

    val futureJsonArray: Future[JsArray] = users.map { documents =>
      Json.arr(Json.toJson(documents))
    }

    futureJsonArray.map {
      documents =>
        Ok(documents(0))
    }
  }

  def createDocument = Action.async(parse.json) {
    println("in createDocument")
    request =>
      val b = request.body
      val v = request.body.validate[User]

      if (v.isInstanceOf[JsError]) {
        logger.error(v.isError.toString)
        Future.successful(BadRequest("invalid json " + v))
      }
      else {
      UserDao.insert(v.get).map {
        lastError =>
          logger.error(s"Successfully inserted with LastError: $lastError")
          Created("Document Created")
      }.recover {
        case LastError(ok, err, code, errMsg, originalDocument, updated, updatedExisting) =>
          logger.error("Mongo error, ok: " + ok + " err: " + err + " code: " + code + " errMsg: " + errMsg)
          if (code.get == 11000) Conflict("Document already exists " + errMsg) else InternalServerError
      }
    }
  }
}
