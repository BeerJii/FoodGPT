package database

import slick.jdbc.JdbcProfile
import slick.basic.DatabaseConfig
import scala.concurrent.{ExecutionContext, Future}
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import play.api.Logging
import slick.jdbc.PostgresProfile.api._


@Singleton
class Schema @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends Logging {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val db = dbConfig.db



  def createSchema(): Future[Unit] = {
    logger.info("🚀 Attempting to create database schema...")

    db.run(FoodMacroTable.query.schema.create) // 🔥 Use `.create` instead of `.createIfNotExists`
      .map(_ => logger.info("Database schema created successfully."))
      .recover { case ex =>
        logger.error("Schema creation failed!", ex)
      }
  }
}
