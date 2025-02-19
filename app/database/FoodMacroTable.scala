package database

import slick.jdbc.PostgresProfile.api._
import models.FoodMacro
import java.sql.Timestamp

class FoodMacroTable(tag: Tag) extends Table[FoodMacro](tag, "food_macros") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def inputType = column[String]("input_type")
  def inputContent = column[String]("input_content")
  def protein = column[Double]("protein")
  def carbs = column[Double]("carbs")
  def fat = column[Double]("fat")
  def createdAt = column[Timestamp]("created_at", O.Default(new Timestamp(System.currentTimeMillis())))

  def * = (id.?, inputType, inputContent, protein, carbs, fat, createdAt) <> ((FoodMacro.apply _).tupled, FoodMacro.unapply)
}

//Ensure query object is correctly defined
object FoodMacroTable {
  val query = TableQuery[FoodMacroTable]
}
