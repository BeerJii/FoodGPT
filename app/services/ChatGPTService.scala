package services

import sttp.client3._
import sttp.client3.akkahttp.AkkaHttpBackend
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject
import models.{FoodMacroRequest, FoodMacroResponse}
import sttp.model.StatusCode

class ChatGPTService @Inject()(implicit ec: ExecutionContext) {

  private val apiKey = "-" //API Key
  private val apiUrl = "https://api.openai.com/v1/chat/completions"
  private val backend = AkkaHttpBackend() //BackendInitialisation

  def extractMacros(foodRequest: FoodMacroRequest): Future[Option[FoodMacroResponse]] = {
    val base64Data: Option[String] = foodRequest.base64Image.map(_.replaceFirst("^data:image/\\w+;base64,", ""))
    //different cases
    val prompt: String = foodRequest.inputType match {
      case "text" =>
        s"""Analyze this food item and return the macronutrient values **along with** a brief food description and calorie count.
           |Provide a JSON response with:
           |- `"description"`: Short description of the food
           |- `"calories"`: Total calories
           |- `"protein"`, `"carbs"`, `"fat"`: Macronutrients
           |
           |Food item: ${foodRequest.inputContent}
           |""".stripMargin

      case "image" =>
        s"""Analyze this food image and return the macronutrient values **along with** a food description and calorie count.
           |Provide a JSON response with:
           |- `"description"`: Short description of the food
           |- `"calories"`: Total calories
           |- `"protein"`, `"carbs"`, `"fat"`: Macronutrients
           |""".stripMargin

      case "image+text" =>
        s"""Analyze this food item using both the image and text description, and return the macronutrient values **along with** a food description and calorie count.
           |Provide a JSON response with:
           |- `"description"`: Short description of the food
           |- `"calories"`: Total calories
           |- `"protein"`, `"carbs"`, `"fat"`: Macronutrients
           |
           |Text description: ${foodRequest.inputContent}
           |""".stripMargin

      case _ => "Invalid input type."
    }

    //messageprep
    val messages = base64Data match {
      case Some(image) => Json.arr(
        Json.obj("role" -> "system", "content" -> "You are a food macro calculator. Always return a JSON response."),
        Json.obj("role" -> "user", "content" -> Json.arr(
          Json.obj("type" -> "text", "text" -> prompt),
          Json.obj("type" -> "image_url", "image_url" -> Json.obj("url" -> s"data:image/jpeg;base64,$image"))
        ))
      )
      case None => Json.arr(
        Json.obj("role" -> "system", "content" -> "You are a food macro calculator. Always return a JSON response."),
        Json.obj("role" -> "user", "content" -> prompt)
      )
    }

    val requestBody = Json.obj(
      "model" -> "gpt-4o-mini-2024-07-18", //latest model
      "messages" -> messages
    )

    val apiRequest = basicRequest
      .post(uri"$apiUrl")
      .header("Authorization", s"Bearer $apiKey")
      .header("Content-Type", "application/json")
      .body(Json.stringify(requestBody))

    Future(apiRequest.send(backend)).flatMap { futureResponse =>
      futureResponse.map { response =>
        if (response.code == StatusCode.Ok) {
          val jsonString = response.body.getOrElse("{}")
          println(s"OpenAI API Response: $jsonString")

          val json = Json.parse(jsonString)

          (json \ "choices" \ 0 \ "message" \ "content").asOpt[String] match {
            case Some(content) =>
              val cleanedContent = content.trim.stripPrefix("```json").stripPrefix("```").stripSuffix("```").trim
              println(s"Extracted JSON content: $cleanedContent")
              parseResponse(cleanedContent) match {
                case Some(macros) => Future.successful(Some(macros))
                case None =>
                  println("Unable to parse JSON response.")
                  Future.successful(None)
              }
            case None =>
              println("⚠️ 'content' field missing in choices.")
              Future.successful(None)
          }
        } else {
          println(s"API request failed with status ${response.code}: ${response.body.getOrElse("No response body")} ")
          Future.successful(None)
        }
      }.flatten
    }
  }

  private def parseResponse(response: String): Option[FoodMacroResponse] = {
    try {
      val cleanedJson = response.trim.stripPrefix("```json").stripPrefix("```").stripSuffix("```").trim
      val json = Json.parse(cleanedJson)

      for {
        description <- (json \ "description").asOpt[String]
        calories <- (json \ "calories").asOpt[Double]
        protein <- (json \ "protein").asOpt[Double]
        carbs <- (json \ "carbs").asOpt[Double]
        fat <- (json \ "fat").asOpt[Double]
      } yield FoodMacroResponse(description, calories, protein, carbs, fat, "API Works")
    } catch {
      case e: Exception =>
        println(s"⚠️ JSON Parsing Error: ${e.getMessage}")
        None
    }
  }
}
