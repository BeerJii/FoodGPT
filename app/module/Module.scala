package module

import com.google.inject.{AbstractModule, Provides, Singleton}
import play.api.{Configuration, Environment}
import database.Schema
import slick.jdbc.JdbcBackend.Database
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import play.api.Logging

class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule with Logging {

  override def configure(): Unit = {
    bind(classOf[Schema]).asEagerSingleton()
  }

  @Provides @Singleton
  def provideDatabase(): Database = {
    Database.forConfig("slick.dbs.default.db")
  }

  @Provides @Singleton
  def onStart(schema: Schema)(implicit ec: ExecutionContext): Future[Unit] = {
    logger.info("🚀 Initializing database schema...")
    schema.createSchema()
  }
}
