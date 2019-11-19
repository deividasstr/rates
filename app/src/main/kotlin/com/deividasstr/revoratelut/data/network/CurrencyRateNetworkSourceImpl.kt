package com.deividasstr.revoratelut.data.network

import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

class CurrencyRateNetworkSourceImpl(
    private val currencyRatesClient: CurrencyRatesClient
) : CurrencyRateNetworkSource {

    override fun getCurrencyToRateFlow(baseCurrency: Currency): Flow<List<CurrencyWithRate>> {
        val currencyCode = baseCurrency.currencyCode
        return flow {
            while (true) {
                val currentRates = currencyRatesClient
                    .getCurrentRates(currencyCode)
                    .toCurrencyWithRatioList()
                emit(currentRates)

                // If flow is not observed anymore, delay will cancel
                delay(TimeUnit.SECONDS.toMillis(1))
            }
        }
    }

    private fun CurrencyRatesResponse.toCurrencyWithRatioList(): List<CurrencyWithRate> {
        return rates.map(::rateMapToCurrencyWithRatioList)
    }

    private fun rateMapToCurrencyWithRatioList(currencyToRate: Map.Entry<String, Double>)
        : CurrencyWithRate {
        val currency = Currency(currencyToRate.key)
        val ratio = currencyToRate.value.toBigDecimal()
        return CurrencyWithRate(currency, ratio)
    }
}