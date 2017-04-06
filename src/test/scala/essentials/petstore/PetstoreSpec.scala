package essentials.petstore

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import essentials.petstore.domain.{Pet, PetRecord}
import essentials.petstore.server.ModelFormatters
import org.joda.time.DateTime

class PetstoreSpec extends UnitSpec with ModelFormatters with ScalatestRouteTest {

  object TestableApp extends AppModule {
    // We could mock or override any of the vals declared in the AppModule
    // here if we needed to!
  }

  import TestableApp._

  override def beforeAll(): Unit = {
    // Ensure our database gets initialized
    dbInit.initDatabaseTables()
  }

  "Petstore" should {
    "get listing of all pets" in {
      Get("/pets") ~> restServer.route ~> check {
        responseAs[Seq[Pet]] should have size 0
      }
    }

    "save listing of a pet cat" in {
      val pet = Pet("snuggles", "cat", 12, fixed = true)
      Post("/pets", pet) ~> restServer.route ~> check {
        status should be(OK)
      }
    }

    "not save listing of a pet alligator" in {
      val pet = Pet("bitey", "alligator", 3, fixed = false)
      Post("/pets", pet) ~> restServer.route ~> check {
        status should be(UnprocessableEntity)
      }
    }

    "only see 1 pet listing" in {
      Get("/pets") ~> restServer.route ~> check {
        responseAs[Seq[Pet]] should have size 1
      }
    }

    "attach record to pet" in {
      val pet = Get("/pets") ~> restServer.route ~> check {
        val response = responseAs[Seq[Pet]]
        response should have size 1
        response.head
      }

      val petRecord = PetRecord(Some("Going to a loving home."), Some(new DateTime()), pet.id.getOrElse(throw new IllegalArgumentException))
      Post("/records", petRecord) ~> restServer.route ~> check {
        status should be(OK)
      }
    }

    // TODO test our /pets GET route to ensure the intact param works as intended
  }
}