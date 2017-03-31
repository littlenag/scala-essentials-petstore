package essentials.petstore.database

import essentials.petstore.domain._
import slick.basic.DatabaseConfig
import slick.jdbc.meta.MTable
import slick.jdbc.JdbcProfile
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try

// https://codequs.com/p/B1IogRLY/scala-tutorial-create-crud-with-slick-and-mysql/

trait Db {
  val dbConfig: DatabaseConfig[JdbcProfile]
  val db: JdbcProfile#Backend#Database = dbConfig.db
}

trait PetsTable { this: Db =>
  import dbConfig.profile.api._

  class Pets(tag: Tag) extends Table[Pet](tag, "pets") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def species = column[String]("species")

    def age = column[Int]("age")

    def fixed = column[Boolean]("fixed")

    def * = (name, species, age, fixed, id) <> (Pet.tupled, Pet.unapply)
  }

  val pets = TableQuery[Pets]
}

class PetsRepository(val dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) extends Db with PetsTable {
  import dbConfig.profile.api._

  def getAllPets: Future[Try[Seq[Pet]]] = db.run(pets.result.asTry)

  def getPets(includeIntact:Rep[Boolean]): Future[Try[Seq[Pet]]] = {
    val petsQuery = pets.filter(row => includeIntact || row.fixed)
    db.run(petsQuery.result.asTry)
  }

  def saveOne(petToAdd: Pet): Future[Pet] = {
    db.run(pets returning pets.map(_.id) += petToAdd).map(id => petToAdd.copy(id = id))
  }

  def saveAll(petsToAdd: Seq[Pet]): Future[Try[Option[Int]]] = {
    val saveAction = pets ++= petsToAdd
    db.run(saveAction.asTry)
  }
}

/**
  * Lets us save and retrieve data from a database.
  */
class DatabaseInitializer(val dbConfig: DatabaseConfig[JdbcProfile], petsRepo:PetsRepository) extends Db with LazyLogging {
  import dbConfig.profile.api._

  def initDatabaseTables(): Unit = {
    logger.info("Setting up database")
    // Get all existing tables
    val tables = Await.result(db.run(MTable.getTables), 10 seconds)

    val petsTableName = petsRepo.pets.baseTableRow.tableName
    if (!tables.exists(existingTable => existingTable.name.name == petsTableName)) {
      logger.info(s"Creating table '$petsTableName'")
      Await.result(db.run(petsRepo.pets.schema.create), 10 seconds)
    } else {
      logger.info(s"Table '$petsTableName' already exists")
    }
    logger.info("Finished setting up database")
  }
}
