package essentials.petstore.database

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

// https://codequs.com/p/B1IogRLY/scala-tutorial-create-crud-with-slick-and-mysql/

trait Db {
  val dbConfig: DatabaseConfig[JdbcProfile]
  val db: JdbcProfile#Backend#Database = dbConfig.db
}






