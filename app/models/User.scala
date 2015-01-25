package models

import play.libs.Scala
import be.objectify.deadbolt.core.models.Subject
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.BSONFormats._
import reactivemongo.bson.BSONObjectID
import reactivemongo.extensions.json.dao.JsonDao
import reactivemongo.api.indexes.{ Index, IndexType }



/**
 *
 * @author Steve Chaloner (steve@objectify.be)
 */

package users {

import be.objectify.deadbolt.core.models.Permission

case class UserPermission(val value: String) extends Permission
{
  def getValue: String = value
}

case class SecurityRole(val roleName: String)
  extends be.objectify.deadbolt.core.models.Role
{
  def getName: String = roleName
}

case class User(name: String,
                  description: String,
                  roles: List[SecurityRole],
                  permissions: List[UserPermission]
                   ) extends Subject {

//    var roleList: List[SecurityRole] = List[SecurityRole]();

    def getRoles: java.util.List[SecurityRole] = {

//      for (n <- roles) {
//        roleList = new SecurityRole(n) :: roleList;
//      }
      Scala.asJava(roles)
    }

    def getPermissions: java.util.List[UserPermission] = {
      Scala.asJava(List(new UserPermission("printers.edit")))
    }

    def getIdentifier: String = name

  }

  object User {
    implicit val roleFormat = Json.format[SecurityRole]
    implicit val userPermissionFormat = Json.format[UserPermission]
    implicit val userFormat = Json.format[User]
  }

  object JsonFormats {
    //  import play.api.libs.json.Json

    import play.api.data.Form
    import play.api.data.Forms._

    implicit val roleFormat = Json.format[SecurityRole]
    implicit val userPermissionFormat = Json.format[UserPermission]
    implicit val userFormat = Json.format[User]

    val userForm = Form(
      mapping(
        "name" -> text,
        "description" -> text,
        "roles" -> list(mapping("roleName" -> text)(SecurityRole.apply)(SecurityRole.unapply)),
        "permissions" -> list(mapping("value" -> text)(UserPermission.apply)(UserPermission.unapply))
      )(User.apply)(User.unapply)
    )

  }

  object UserDao extends {
    override val autoIndexes = Seq(
      Index(Seq("name" -> IndexType.Ascending), unique = true, background = true)
    )
  } with JsonDao[User, BSONObjectID](ReactiveMongoPlugin.db, "users") {
    // some high level db functions
  }

}