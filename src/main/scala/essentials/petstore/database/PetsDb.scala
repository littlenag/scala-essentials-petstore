package essentials.petstore.database

import essentials.petstore.domain._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait PetsTable { this: Db =>
  import dbConfig.profile.api._

  class Pets(tag: Tag) extends Table[Pet](tag, "pets") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def species = column[String]("species")

    def age = column[Int]("age")

    def fixed = column[Boolean]("fixed")

    def * = (name, species, age, fixed, id.?) <> (Pet.tupled, Pet.unapply)
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
    db.run(pets returning pets.map(_.id) += petToAdd).map(id => petToAdd.copy(id = Some(id)))
  }

  def saveAll(petsToAdd: Seq[Pet]): Future[Try[Option[Int]]] = {
    val saveAction = pets ++= petsToAdd
    db.run(saveAction.asTry)
  }
}