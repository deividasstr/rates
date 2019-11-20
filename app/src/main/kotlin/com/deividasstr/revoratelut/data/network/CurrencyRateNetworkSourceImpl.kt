package com.deividasstr.revoratelut.data.network

import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import com.deividasstr.revoratelut.extensions.periodicalFlowFrom
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class CurrencyRateNetworkSourceImpl(
    private val currencyRatesClient: CurrencyRatesClient
) : CurrencyRateNetworkSource {

    override fun getCurrencyRateFlow(
        baseCurrency: Currency
    ): Flow<NetworkResultWrapper<List<CurrencyWithRate>>> {
        val currencyCode = baseCurrency.currencyCode
        return periodicalFlowFrom(TimeUnit.SECONDS.toMillis(1)) {
            NetworkCoroutineHelper.safeApiCall {
                val response = currencyRatesClient.getCurrentRates(currencyCode)
                response.toCurrencyWithRatioList()
            }
        }
    }

    // Would be nice to separate to a abstracted mapper class. Or even map it in Repo
    private fun CurrencyRatesResponse.toCurrencyWithRatioList(): List<CurrencyWithRate> {
        return rates.map(::rateMapToCurrencyWithRatioList)
    }

    private fun rateMapToCurrencyWithRatioList(
        currencyToRate: Map.Entry<String, Double>
    ): CurrencyWithRate {
        val currency = Currency(currencyToRate.key)
        val ratio = currencyToRate.value.toBigDecimal()
        return CurrencyWithRate(currency, ratio)
    }
}