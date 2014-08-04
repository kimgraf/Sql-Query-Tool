package controllers

import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.indexes._
import traits.DocumentControllerTrait
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import play.api.libs.json._
import models._
import models.JsonFormats.jdbcDriverFormat
import java.io._
import javax.inject.Singleton

@Singleton
class JdbcDrivesController extends DocumentControllerTrait[JdbcDriverConfig] {
  implicit var format : Format[JdbcDriverConfig] = jdbcDriverFormat

  def collection: JSONCollection = db.collection[JSONCollection]("jdbcdrivers")
  collection.indexesManager.ensure(
    Index(List("name" -> IndexType.Ascending), unique = true)
  )

  def upload = Action(parse.raw) { request =>
    val name = request.headers.get("File-Name").get
    val d = new File("jdbcdrivers")
    if(!d.exists()){
      d.mkdir()
    }
    val r = request.body
    logger.debug(r.size.toString)
    val f = r.asFile
    f.renameTo(new File("jdbcdrivers/" + name))
//    request.body.moveTo(new File("jdbcdrivers/" + name))
    Ok("File uploaded")
  }

}
