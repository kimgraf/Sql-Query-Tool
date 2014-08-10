package services.jdbcquery


import java.sql.{ResultSetMetaData, ResultSet, Connection, DriverManager}

import models.{JdbcDriverConfig,JdbcDatabaseConfig}
import org.slf4j.{LoggerFactory, Logger}
import play.api.libs.json.Json
import services.dynamicdriver._
import java.io.File
import play.api.libs.json._

import scala.collection.mutable.ListBuffer

/**
 * Created by kim on 06/08/14.
 */
object QueryManger {
  final val logger: Logger = LoggerFactory.getLogger(this.getClass)
  var err: JsValue = null

  def testConnection(driver: JdbcDriverConfig, database: JdbcDatabaseConfig) : String = {
      var con: Connection = getDbConnection(driver, database)
      if(con == null)
        return s"Test connection failed for database ${database.name}<br>with error ${err}"

      try {
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
        closeConnection(con)
      }

  }

  def executeQuery(driver: JdbcDriverConfig, database: JdbcDatabaseConfig, query: String) : JsValue = {
    var con: Connection = getDbConnection(driver, database)
    if(con == null)
      return JsNull
    try {
      var r : JsObject = null
      val statement = con.createStatement()
      val resultSet = statement.executeQuery(query)
      r = resultSetToJson(resultSet)
//      while (resultSet.next()) {
//        val host = resultSet.getString("question")
//        val user = resultSet.getString("pub_date")
//        r :+  Json.obj(("row", "question, pub_date = " + host + ", " + user + "<br>"))
//        println("question, pub_date = " + host + ", " + user)
//      }
      return r
    } catch {
      case e: AnyRef => {
        logger.debug(e.toString)
        err = Json.obj("error" -> s"Test connection failed for database ${database.name}<br>with error ${e.toString}")
        return null
      }
    } finally {
      closeConnection(con)
    }
  }

  def resultSetToJson(resultSet: ResultSet) : JsObject = {
//    val metadata =
    val jsonObject = Json.obj(
      "columns" -> getColumnMetadata(resultSet.getMetaData),
      "rows" -> getRows(resultSet)
    )
    return jsonObject
  }

  def getRows(resultSet : ResultSet) : JsValue = {
    val fieldCnt = resultSet.getMetaData.getColumnCount

    var rows: JsArray = Json.arr()

    while ( resultSet.next() ) {
      var row = Json.obj()
        1 to fieldCnt foreach
        {i =>
          row = row ++ Json.obj(resultSet.getMetaData.getColumnName(i).toString -> resultSet.getObject(i).toString)
        }
        rows = rows ++ Json.arr(row)
      }

    return rows
  }

  def getColumnMetadata(metadata : ResultSetMetaData) : JsValue = {

    var columns: JsArray = Json.arr()

    1 to metadata.getColumnCount foreach { i =>
      val j = Json.obj("name" -> metadata.getColumnName(i),
        "label" -> metadata.getColumnLabel(i),
        "type" -> metadata.getColumnTypeName(i),
        "precision" -> metadata.getPrecision(i)
      )
      columns = columns ++ Json.arr(j)
    }

    val obj = Json.obj ( "columns" -> columns)


    return columns
  }



  def getDbConnection(driver: JdbcDriverConfig, database: JdbcDatabaseConfig) : Connection = {
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

      return con
      //      val query = Json.obj(("name", database.driver))
    } catch {
      case e: AnyRef => {
        logger.debug(e.toString)
        err = Json.obj("error" -> s"Connection failed for database ${database.name}<br>with error ${e.toString}")
        return null
      }
    }
  }

  def closeConnection(con: Connection) : Unit = {
    if (con != null)
      con.close()
//    dynamicDriver.deregister()
  }

}
