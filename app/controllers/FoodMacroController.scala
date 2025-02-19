package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import services.ChatGPTService
import database.FoodMacroTable
import models.{FoodMacro, FoodMacroRequest, FoodMacroResponse}
import slick.jdbc.PostgresProfile.api._

@Singleton
class FoodMacroController @Inject()(cc: ControllerComponents, chatGPTService: ChatGPTService, db: Database)
                                   (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def analyzeFood = Action.async(parse.json) { request =>
    request.body.validate[FoodMacroRequest] match {
      case JsSuccess(foodRequest, _) =>
        chatGPTService.extractMacros(foodRequest).flatMap {
          case Some(response) =>
            val foodMacro = FoodMacro(
              id = None,
              inputType = foodRequest.inputType,
              inputContent = foodRequest.inputContent,
              protein = response.protein,
              carbs = response.carbs,
              fat = response.fat
            )

            db.run(FoodMacroTable.query += foodMacro).map { _ =>
              Ok(Json.toJson(response))
            }

          case None => Future.successful(BadRequest(Json.obj("error" -> "Failed to extract food macros.")))
        }

      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON format")))
    }
  }

  def getAllMacros = Action.async {
    db.run(FoodMacroTable.query.result).map { macros =>
      Ok(Json.toJson(macros))
    }
  }
}
