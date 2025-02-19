package models

import play.api.libs.json._
import java.sql.Timestamp
import java.text.SimpleDateFormat

case class FoodMacro(
                      id: Option[Long] = None, // Auto-generated primary key
                      inputType: String, // "text", "image", or "image_with_text"
                      inputContent: String, // The actual text or image URL
                      protein: Double,
                      carbs: Double,
                      fat: Double,
                      createdAt: Timestamp = new Timestamp(System.currentTimeMillis()) // Auto-filled timestamp
                    )

// JSON serialization
object FoodMacro {

  // Custom formatter for Timestamp
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    private val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override def writes(ts: Timestamp): JsValue = JsString(dateFormat.format(ts))

    override def reads(json: JsValue): JsResult[Timestamp] = json match {
      case JsString(s) =>
        try {
          JsSuccess(new Timestamp(dateFormat.parse(s).getTime))
        } catch {
          case _: Exception => JsError("Invalid timestamp format")
        }
      case _ => JsError("Expected timestamp as string")
    }
  }

  implicit val format: OFormat[FoodMacro] = Json.format[FoodMacro]
}
