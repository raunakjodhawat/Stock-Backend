package controllers

import models.{CompanyInformation, StockSummary}

import javax.inject._
import play.api.libs.json.{JsSuccess, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.duration.DurationInt
import scala.concurrent._
import ExecutionContext.Implicits.global
@Singleton
class StockController @Inject() (
    ws: WSClient,
    val controllerComponents: ControllerComponents
) extends BaseController {

  implicit val companyInfoReads = Json.reads[CompanyInformation]
  implicit val stockSummaryReads = Json.reads[StockSummary]

  def request(path: String, tickerSymbol: String) = ws
    .url(s"https://finnhub.io/api/v1/$path")
    .addHttpHeaders("Accept" -> "application/json")
    .withRequestTimeout(5000.millis)
    .addQueryStringParameters(
      "token" -> sys.env.get("API_KEY").getOrElse("NONE"),
      "symbol" -> tickerSymbol
    )
    .get()

  def companyInfo(tickerSymbol: String) = Action.async {
    request("stock/profile2", tickerSymbol)
      .map { response =>
        response.json.validate[CompanyInformation] match {
          case JsSuccess(_, _) => Ok(response.json)
          case _               => BadRequest
        }
      }
  }

  def companyQuote(tickerSymbol: String) = Action.async {
    request("quote", tickerSymbol)
      .map { response =>
        response.json.validate[StockSummary] match {
          case JsSuccess(_, _) => Ok(response.json)
          case _               => BadRequest
        }
      }
  }
}
