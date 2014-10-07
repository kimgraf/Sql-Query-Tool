package controllers

import javax.inject.{Singleton, Inject}
import org.slf4j.{LoggerFactory, Logger}
import play.api._
import play.api.mvc._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import models._
import java.util.Properties
import play.api.Play.current
import play.api.libs.iteratee.Enumerator
import java.lang.reflect.Method
import play.api.templates.Html
import models._
import play.api.http.Writeable
import org.slf4j.{LoggerFactory, Logger}

object Dynamic {
  final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def render(keyword: String): Option[play.twirl.api.Html] = {
    renderDynamic("views.html." + keyword)
  }

  def renderDynamic(viewClazz: String): Option[play.twirl.api.Html] = {
    try {
      val clazz: Class[_] = Play.current.classloader.loadClass(viewClazz)
      println(clazz.getMethods())
      val render: Method = clazz.getDeclaredMethod("render")
      val view = render.invoke(clazz).asInstanceOf[play.twirl.api.Html]
      return Some(view)
    } catch {
      case ex: ClassNotFoundException => logger.error("Html.renderDynamic() : could not find view " + viewClazz, ex)
    }

    return None
  }
}

/**
 * Created by kim on 02/09/14.
 */
@Singleton
class TemplateController extends Controller {

  def getTemplate(name: String) = Action {

//    Ok(views.html.templates.QueryList())
    Dynamic.render( name) match {
      case Some(i) => Ok(i)
      case None => NotFound
    }

  }

}
