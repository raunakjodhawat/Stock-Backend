package controllers

import models.{Candle, CompanyInformation, News, Recommendation, StockSummary}

import javax.inject._
import play.api.libs.json.{JsSuccess, Json, Writes}
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
  implicit val recommendationReads = Json.reads[Recommendation]
  implicit val candleReads = Json.reads[Candle]
  implicit val newsReads = Json.reads[News]
  implicit val recommendationWrites = new Writes[Recommendation] {
    def writes(recommendation: Recommendation) = Json.obj(
      "buy" -> recommendation.buy,
      "hold" -> recommendation.hold,
      "strongBuy" -> recommendation.strongBuy,
      "sell" -> recommendation.sell,
      "strongSell" -> recommendation.strongSell
    )
  }

  def request(path: String, tickerSymbol: String) = ws
    .url(s"https://finnhub.io/api/v1/$path")
    .addHttpHeaders("Accept" -> "application/json")
    .withRequestTimeout(5000.millis)
    .addQueryStringParameters(
      "token" -> sys.env.get("API_KEY").getOrElse("NONE"),
      "symbol" -> tickerSymbol
    )

  def companyInfo(tickerSymbol: String) = Action.async {
    request("stock/profile2", tickerSymbol)
      .get()
      .map { response =>
        response.json.validate[CompanyInformation] match {
          case JsSuccess(_, _) => Ok(response.json)
          case _               => BadRequest
        }
      }
  }

  def companyQuote(tickerSymbol: String) = Action.async {
    request("quote", tickerSymbol)
      .get()
      .map { response =>
        response.json.validate[StockSummary] match {
          case JsSuccess(_, _) => Ok(response.json)
          case _               => BadRequest
        }
      }
  }

  def companyRecommendation(tickerSymbol: String) = Action.async {
    request("stock/recommendation", tickerSymbol)
      .get()
      .map { response =>
        response.json.validate[Array[Recommendation]] match {
          case JsSuccess(x, _) if x.length > 0 => Ok(Json.toJson(x(0)))
          case _                               => BadRequest
        }
      }
  }
  def charts(
      tickerSymbol: String,
      resolution: String,
      from: String,
      to: String
  ) =
    Action.async {
      request("stock/candle", tickerSymbol)
        .addQueryStringParameters(
          "resolution" -> resolution,
          "from" -> from,
          "to" -> to
        )
        .get()
        .map { response =>
          response.json.validate[Candle] match {
            case JsSuccess(_, _) => Ok(response.json)
            case _               => BadRequest
          }
        }
    }

  def news(
      tickerSymbol: String,
      from: String,
      to: String
  ) =
    Action.async {
      request("company-news", tickerSymbol)
        .addQueryStringParameters(
          "from" -> from,
          "to" -> to
        )
        .get()
        .map { response =>
          response.json.validate[Seq[News]] match {
            case JsSuccess(x, _) if x.length > 0 => Ok(response.json)
            case _                               => BadRequest
          }
        }
    }
}
