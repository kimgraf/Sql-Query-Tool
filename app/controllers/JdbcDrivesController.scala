package controllers

import play.api.libs.Files.TemporaryFile
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.indexes._
import traits.DocumentControllerTrait
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.api.libs.json._
import models._
//import models.JsonFormats.jdbcDriverFormat
import java.io._
import javax.inject.Singleton
import java.io.File

@Singleton
class JdbcDrivesController extends DocumentControllerTrait[JdbcDriverConfig] {
  implicit var format : Format[JdbcDriverConfig] = Json.format[JdbcDriverConfig]

  def collection: JSONCollection = db.collection[JSONCollection]("jdbcdrivers")
  collection.indexesManager.ensure(
    Index(List("name" -> IndexType.Ascending), unique = true)
  )

  def upload = Action(parse.multipartFormData) { request =>
    doUpload(request)
  }

  def doUpload(request:Request[MultipartFormData[TemporaryFile]]) : Result = {
    request.body.file("jarFile").map { jar =>
      val filename = jar.filename
      val contentType = jar.contentType
      jar.ref.moveTo(new File("jdbcdrivers/" + jar.filename))
      Ok("File upload succeded")
    }.getOrElse {
      BadRequest("File upload failed")
    }
  }

}
