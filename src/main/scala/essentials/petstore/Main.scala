package essentials.petstore

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import essentials.petstore.server.AppServer

/**
  * @author Mark Kegel (mkegel@vast.com)
  */
object Main extends LazyLogging {
  def main(args: Array[String]): Unit = {

    // Load petstore.conf
    val config = ConfigFactory.load("petstore.conf")

    logger.info(s"Let's start the server :-)")

    AppServer.startServer(config.getString("petstore.server.host"), config.getInt("petstore.server.port"))
  }
}
