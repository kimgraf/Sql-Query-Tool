package models
import reactivemongo.bson._
import play.modules.reactivemongo.json.BSONFormats._

case class JdbcDriverConfig (name: String,
                             driverClass: String,
                             driverFiles: List[String])

case class JdbcDatabaseConfig(name: String,
                               driver: String,
                               url: String,
                               userName: Option[String],
                               password: Option[String])

case class SqlQuery(name: String,
                     queryText: String,
                     database: String,
                     description: Option[String])



