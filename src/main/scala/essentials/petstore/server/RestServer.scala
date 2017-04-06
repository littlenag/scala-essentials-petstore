package essentials.petstore.server

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{StatusCodes, _}
import akka.http.scaladsl.server.{ExceptionHandler, HttpApp, RejectionHandler, Route}
import akka.http.scaladsl.model.HttpResponse
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.OFormat
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import essentials.petstore.domain._
import essentials.petstore.database.{PetRecordsRepository, PetsRepository}

import scala.concurrent.{ExecutionContext, Future}

/**
  * The server lets clients save and view persons.
  */
class RestServer(petsRepo: PetsRepository, petRecordsRepo:PetRecordsRepository) extends HttpApp with ModelFormatters with LazyLogging {

  import petsRepo.dbConfig.profile.api._

  case class ValidationException(message:String) extends Exception(message)

  /** Defines the HTTP requests our server accepts and how it responds. */
  override def route: Route =
    handleExceptions(exceptionHandler) {
      handleRejections(rejectionHandler) {
        (path("pets") & parameter('intact.as[Boolean] ? false)) { wantIntact =>
          // TODO add query parameters so that we can filter on age and species
          get {
            complete(petsRepo.getPets(wantIntact))
          } ~
          post {
            entity(as[Pet]) { pet =>
              // TODO this isn't a very type-safe way to validate
              pet.species match {
                case "cat" | "dog" => complete(petsRepo.saveOne(pet))
                case other => failWith(ValidationException(s"Unrecognized species: $other"))
              }
            }
          }
          // TODO Seem to be missing Update and Delete routes for Pets
        } ~
        path("records") {
          get {
            complete(petRecordsRepo.getAll)
          } ~
          post {
            entity(as[PetRecord]) { record =>
              complete(petRecordsRepo.saveOne(record))
            }
          }
        }
        // TODO Add routes+logic to handle Pet Owners as well
      }
    }

  private lazy val exceptionHandler = ExceptionHandler {
    case ValidationException(msg) =>
      complete(HttpResponse(StatusCodes.UnprocessableEntity, entity = msg))
    case ex : Exception =>
      complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Bad result: ${ex.getMessage}"))
  }

  /**
    * Handles the case when the client tries to visit a page of this application that doesn't exist. More generally,
    * we send this respond for every HTTP request that can't be handled by the server's routes.
    */
  private lazy val rejectionHandler = RejectionHandler.default

  /** The default implementation shuts the server down immediately after startup. Instead, run the server until its
    * it is shutdown with Ctrl-C or another system signal. */
  override def waitForShutdownSignal(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext): Future[Done] = {
    logger.info("Press Ctrl-C to shutdown the server...")
    Future.never
  }

}

trait ModelFormatters extends PlayJsonSupport {
  import play.api.libs.json.Json

  // TODO add validation to petFormat, for example age < 0 shouldn't be valid

  // JSON formatters (i.e. convert object to/from JSON) that we can use
  implicit val petFormat: OFormat[Pet] = Json.format[Pet]
  implicit val petRecordsFormat: OFormat[PetRecord] = Json.format[PetRecord]
}

object ModelFormatters extends ModelFormatters
