package models

import play.api.libs.json._

case class FoodMacroResponse(
                              description: String,
                              calories: Double,
                              protein: Double,
                              carbs: Double,
                              fat: Double,
                              message: String
                            )

// JSON Serialisation
object FoodMacroResponse {
  implicit val format: OFormat[FoodMacroResponse] = Json.format[FoodMacroResponse]
}
