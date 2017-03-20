package essentials.petstore.database

import java.util.UUID

import essentials.petstore.domain.Models._
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.meta.MTable
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try

/**
  * Lets us save and retrieve data from a database.
  */
object AppDb extends LazyLogging {

  // This configuration is in src/main/resources/application.conf
  private val dbConfig = "h2db"
  private val db = Database.forConfig(dbConfig)

  private val pets = TableQuery[PetsTable]

  private def setUpDb(): Unit = {
    logger.info("Setting up database")
    // Get all existing tables
    val tables = Await.result(db.run(MTable.getTables), 10 seconds)

    val petsTableName = pets.baseTableRow.tableName
    if (!tables.exists(existingTable => existingTable.name.name == petsTableName)) {
      logger.info(s"Creating table '$petsTableName'")
      Await.result(db.run(pets.schema.create), 10 seconds)
    } else {
      logger.info(s"Table '$petsTableName' already exists")
    }
    logger.info("Finished setting up database")
  }

  setUpDb()

  class PetsTable(tag: Tag) extends Table[Pet](tag, "pets") {
    def id = column[UUID]("id", O.PrimaryKey)

    def name = column[String]("name")

    def species = column[String]("species")

    def age = column[Int]("age")

    def sex = column[String]("sex")

    def fixed = column[Boolean]("fixed")

    def * = (id, name, species, age, sex, fixed) <> (Pet.tupled, Pet.unapply)
  }

  def getAllPets: Future[Try[Seq[Pet]]] = db.run(pets.result.asTry)

  def save(petsToAdd: Seq[Pet]): Future[Try[Option[Int]]] = {
    val saveAction = pets ++= petsToAdd
    db.run(saveAction.asTry)
  }
}
