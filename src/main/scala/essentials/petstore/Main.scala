package essentials.petstore

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import essentials.petstore.database.{DatabaseInitializer, PetsRepository}
import essentials.petstore.server.RestServer


trait AppModule {
  import com.softwaremill.macwire._
  import slick.basic.DatabaseConfig
  import slick.jdbc.JdbcProfile

  import scala.concurrent.ExecutionContext.Implicits.global

  // Load petstore.conf
  lazy val appConfig = ConfigFactory.load("petstore.conf")

  // This configuration is in src/main/resources/application.conf
  lazy val dbConfig = DatabaseConfig.forConfig[JdbcProfile]("h2db", appConfig)

  lazy val petsRepo = wire[PetsRepository]
  lazy val dbInit = wire[DatabaseInitializer]
  lazy val restServer = wire[RestServer]
}

/**
  * @author Mark Kegel (mkegel@vast.com)
  */
object Main extends LazyLogging with AppModule {
  def main(args: Array[String]): Unit = {

    logger.info(s"Ensure the database tables all exist!")

    dbInit.initDatabaseTables()

    logger.info(s"Let's start the server :-)")

    restServer.startServer(appConfig.getString("petstore.server.host"), appConfig.getInt("petstore.server.port"))
  }
}
