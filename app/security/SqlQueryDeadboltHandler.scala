package security

/**
 * Created by kim on 21/10/14.
 */
import be.objectify.deadbolt.scala.{DynamicResourceHandler, DeadboltHandler}
import play.api.libs.json.Json
import play.api.mvc.{Request, Result, Results}
import be.objectify.deadbolt.core.models.Subject
import models.users._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class SqlQueryDeadboltHandler (dynamicResourceHandler: Option[DynamicResourceHandler] = None) extends DeadboltHandler {

  def beforeAuthCheck[A](request: Request[A]) = None

  override def getDynamicResourceHandler[A](request: Request[A]): Option[DynamicResourceHandler] = {
    if (dynamicResourceHandler.isDefined) dynamicResourceHandler
    else Some(new SqlQueryDynamicResourceHandler())
  }

  override def getSubject[A](request: Request[A]): Future[Option[Subject]] = {
    // e.g. request.session.get("user")
     UserDao.findOne(Json.obj( "name" -> "kim") )
  }

  override def onAuthFailure[A](request: Request[A]): Future[Result] = {
    Future {Results.Forbidden(views.html.accessFailed())}
  }

}

