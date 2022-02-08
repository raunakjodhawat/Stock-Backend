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
