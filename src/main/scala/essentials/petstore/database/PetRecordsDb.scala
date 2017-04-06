package essentials.petstore.database

import java.sql.Timestamp

import essentials.petstore.domain._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}


trait PetRecordsTable { this: Db with PetsTable =>
  import dbConfig.profile.api._

  implicit def dateTime =
    MappedColumnType.base[DateTime, Timestamp](
      dt => new Timestamp(dt.getMillis),
      ts => new DateTime(ts.getTime)
    )

  class Records(tag: Tag) extends Table[PetRecord](tag, "records") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def adoptionPapers = column[Option[String]]("adoption_papers")

    def lastVetVisit = column[Option[DateTime]]("last_vet_visit")

    def petId = column[Long]("pet_id")

    def * = (adoptionPapers, lastVetVisit, petId, id.?) <> (PetRecord.tupled, PetRecord.unapply)

    def pet = foreignKey("pet_fk", petId, pets)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  }

  val records = TableQuery[Records]
}

class PetRecordsRepository(val dbConfig: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) extends Db with PetsTable with PetRecordsTable {
  import dbConfig.profile.api._

  def getAll: Future[Seq[PetRecord]] = db.run(records.result)

  def saveOne(recordToSave: PetRecord): Future[PetRecord] = {
    val dbAction = records returning records.map(_.id) += recordToSave
    val dbResult = db.run(dbAction)
    dbResult.map(id => recordToSave.copy(id = Some(id)))
  }
}