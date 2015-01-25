package security

/**
 * Created by kim on 21/10/14.
 */
import java.lang.System
import be.objectify.deadbolt.scala.{DynamicResourceHandler, DeadboltHandler}
import collection.immutable.Map
import play.api.mvc.Request

class SqlQueryDynamicResourceHandler extends DynamicResourceHandler
{
  def isAllowed[A](name: String, meta: String, handler: DeadboltHandler, request: Request[A]) = {
    SqlQueryDynamicResourceHandler.handlers(name).isAllowed(name,
      meta,
      handler,
      request)
  }

  def checkPermission[A](permissionValue: String, deadboltHandler: DeadboltHandler, request: Request[A]) = {
    // todo implement this when demonstrating permissions
    false
  }
}

object SqlQueryDynamicResourceHandler {
  val handlers: Map[String, DynamicResourceHandler] =
    Map(
      "pureLuck" -> new DynamicResourceHandler() {
        def isAllowed[A](name: String, meta: String, deadboltHandler: DeadboltHandler, request: Request[A]) =
          System.currentTimeMillis() % 2 == 0

        def checkPermission[A](permissionValue: String, deadboltHandler: DeadboltHandler, request: Request[A]) = false
      }
    )
}
