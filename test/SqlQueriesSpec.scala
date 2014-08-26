//import org.specs2.
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.mvc.Result

import play.api.test.Helpers._
import play.api.test.{FakeRequest, FakeApplication}
import play.api.test._
import controllers.SqlQueriesController
import play.api.libs.json._
import play.mvc.Http.HeaderNames

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * Created by kim on 22/08/14.
 */
class SqlQueriesSpec extends Specification{

  "SqlQueries" should {
    val additionalConfiguration: Map[String, String] = Map("mongodb.uri" -> "mongodb://localhost:27017/test-db")
    def application = FakeApplication(additionalConfiguration = additionalConfiguration)
    step(play.api.Play.start(application))
    val query1 = Json.obj(
      "name" -> "theTestQuery",
      "queryText" -> "some query text",
      "database" -> "db 2",
      "description" -> "the test desctription"
    )

    var deleteRequest = FakeRequest(GET, "/sqlqueries/deletebyname/theTestQuery")

    var request = FakeRequest(POST, "/sqlqueries/create")
      .withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
      .withJsonBody(query1)

    var findRequest = FakeRequest(GET, "/sqlqueries/findbyname/theTestQuery")

    "SqlQueries Should manage CRUD for query" in {
        println("DB Insertion")
      val createdResult = route(request).get
      status(createdResult) must equalTo(CREATED)

      val findResult = route(findRequest).get
      status(findResult) must equalTo(OK)

      val conflictResult = route(request).get
        status(conflictResult) must equalTo(CONFLICT)

      val deletedResult = route(deleteRequest).get
      status(deletedResult) must equalTo(OK)

      val notFindResult = route(findRequest).get
      status(notFindResult) must equalTo(NOT_FOUND)

    }
    step {
      println("Dropping the DB")
      val sqlquerise = new SqlQueriesController()
      Await.result(sqlquerise.collection.drop, Duration(1000, "millis")): Unit
    }
    step(play.api.Play.stop())
  }

}
