package essentials.petstore.database

import slick.basic.DatabaseConfig
import slick.jdbc.meta.MTable
import slick.jdbc.JdbcProfile
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.Await

/**
  * Lets us save and retrieve data from a database.
  */
class DatabaseInitializer(val dbConfig: DatabaseConfig[JdbcProfile], petsRepo: PetsRepository, recordsRepo: PetRecordsRepository) extends Db with LazyLogging {
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

    val recordsTableName = recordsRepo.records.baseTableRow.tableName
    if (!tables.exists(existingTable => existingTable.name.name == recordsTableName)) {
      logger.info(s"Creating table '$recordsTableName'")
      Await.result(db.run(recordsRepo.records.schema.create), 10 seconds)
    } else {
      logger.info(s"Table '$recordsTableName' already exists")
    }

    logger.info("Finished setting up database")
  }
}
