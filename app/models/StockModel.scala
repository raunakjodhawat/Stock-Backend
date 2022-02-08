package models

case class CompanyInformation(
    country: String,
    currency: String,
    exchange: String,
    ipo: String,
    marketCapitalization: Double,
    name: String,
    phone: String,
    shareOutstanding: Double,
    ticker: String,
    weburl: String,
    logo: String,
    finnhubIndustry: String
)

case class StockSummary(
    c: Double,
    d: Double,
    dp: Double,
    h: Double,
    l: Double,
    o: Double,
    pc: Double,
    t: Double
)

case class Recommendation(
    buy: Int,
    hold: Int,
    period: String,
    sell: Int,
    strongBuy: Int,
    strongSell: Int,
    symbol: String
)

case class Candle(
    c: Seq[Double],
    h: Seq[Double],
    l: Seq[Double],
    o: Seq[Double],
    s: String,
    t: Seq[Double],
    v: Seq[Double]
)
