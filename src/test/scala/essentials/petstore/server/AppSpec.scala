package essentials.petstore.server

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import essentials.petstore.UnitSpec
import essentials.petstore.domain.Models.Pet

class AppSpec extends UnitSpec with ModelFormatters with ScalatestRouteTest {

  "Petstore" should {
    "get listing of all pets" in {
      Get("/pets") ~> AppServer.route ~> check {
        responseAs[Seq[Pet]] should have size 0
      }
    }

    "save listing of a pet" in {
      val petKitty = Pet(1, "Snuggles", "Cat", 12, "male", true)
      Post("/pets", petKitty) ~> AppServer.route ~> check {
        status should be(OK)
      }
    }

    "now see listing of 1 pets" in {
      Get("/pets") ~> AppServer.route ~> check {
        responseAs[Seq[Pet]] should have size 1
      }
    }

  }

}
