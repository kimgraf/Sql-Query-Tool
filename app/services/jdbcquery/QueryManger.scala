package services.jdbcquery


import java.sql.Connection

import models.{JdbcDriverConfig,JdbcDatabaseConfig}
import org.slf4j.{LoggerFactory, Logger}
import play.api.libs.json.Json
import services.dynamicdriver._
import java.io.File
import java.sql.DriverManager
/**
 * Created by kim on 06/08/14.
 */
object QueryManger {
  final val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def testConnection(driver: JdbcDriverConfig, database: JdbcDatabaseConfig) : String = {
      val location: String = "jdbcdrivers/" + driver.driverFiles(0)
      var dynamicDriver: DynamicDriverManager = null
      var con: Connection = null

      try {
      dynamicDriver = DynamicDriverManagerFactory.create(
      java.util.Collections.singletonList(new File(location).toURI.toURL),
      driver.driverClass,
      "resources/chaperon.jar")
      con = DriverManager.getConnection(database.url.toString, database.userName.get, database.password.get)

      val dbProduct = con.getMetaData.getDatabaseProductName
      val dbProductVersion = con.getMetaData.getDatabaseProductVersion

      return s"Successfully connected to <strong>${database.name}</strong>:<br><span>     ${dbProduct} ${dbProductVersion}</span>"
        //      val query = Json.obj(("name", database.driver))
    } catch {
      case e: AnyRef => {
        logger.debug(e.toString)
        return s"Test connection failed for database ${database.name}<br>with error ${e.toString}"
      }
    }
      finally {
        if (con != null)
          con.close()
          dynamicDriver.deregister()
      }

  }

}
