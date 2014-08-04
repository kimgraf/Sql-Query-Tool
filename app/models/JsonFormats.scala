package models
import play.modules.reactivemongo.json.BSONFormats._

object JsonFormats {
  import play.api.libs.json.Json

  // Generates Writes and Reads for Feed and JdbcSource thanks to Json Macros
  implicit val jdbcDatabaseFormat = Json.format[JdbcDatabaseConfig]
  implicit val jdbcDriverFormat = Json.format[JdbcDriverConfig]
  implicit val sqlQueryFormat = Json.format[SqlQuery]

}