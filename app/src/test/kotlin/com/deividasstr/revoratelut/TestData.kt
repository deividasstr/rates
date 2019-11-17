package com.deividasstr.revoratelut

import com.deividasstr.revoratelut.data.network.CurrencyRatesResponse
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRatio

object TestData {

    val eur = "EUR"
    val usd = "USD"
    val gbp = "GBP"

    val eurCurrency = Currency(eur)
    val usdCurrency = Currency(usd)
    val gbpCurrency = Currency(gbp)

    const val eurRate: Double = 1.0
    const val usdRate: Double = 1.23
    const val usdRate2: Double = 1.2124
    const val gbpRate: Double = 0.89
    const val gbpRate2: Double = 0.88795

    val eurWithRate = CurrencyWithRatio(eurCurrency, eurRate.toBigDecimal())
    val usdWithRate = CurrencyWithRatio(usdCurrency, usdRate.toBigDecimal())
    val gbpWithRate = CurrencyWithRatio(gbpCurrency, gbpRate.toBigDecimal())

    val currenciesToRates = mapOf(
        eur to eurRate,
        usd to usdRate,
        gbp to gbpRate
    )

    val currenciesToRates2 = mapOf(
        eur to eurRate,
        usd to usdRate2,
        gbp to gbpRate2
    )

    val response = CurrencyRatesResponse("", "", currenciesToRates)
    val response2 = CurrencyRatesResponse("", "", currenciesToRates2)

    val rates = listOf(
        CurrencyWithRatio(eurCurrency, eurRate.toBigDecimal()),
        CurrencyWithRatio(usdCurrency, usdRate.toBigDecimal()),
        CurrencyWithRatio(gbpCurrency, gbpRate.toBigDecimal())
    )

    val rates2 = listOf(
        CurrencyWithRatio(eurCurrency, eurRate.toBigDecimal()),
        CurrencyWithRatio(usdCurrency, usdRate2.toBigDecimal()),
        CurrencyWithRatio(gbpCurrency, gbpRate2.toBigDecimal())
    )

    private val responseRatesMap = mapOf(
        gbp to gbpRate,
        usd to usdRate
    )

    val currencyRatesResponse = CurrencyRatesResponse("EUR", "2018-09-06", responseRatesMap)
}