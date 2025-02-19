package models

import play.api.libs.json._

case class FoodMacroRequest(
                             inputType: String,
                             inputContent: String,
                             base64Image: Option[String]
                           )

// JSON serialization
object FoodMacroRequest {
  implicit val format: OFormat[FoodMacroRequest] = Json.format[FoodMacroRequest]
}
