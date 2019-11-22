package com.deividasstr.revoratelut.data.network

import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import com.deividasstr.revoratelut.extensions.periodicalFlowFrom
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class CurrencyRateNetworkSourceImpl(
    private val currencyRatesClient: CurrencyRatesClient
) : CurrencyRateNetworkSource {

    override fun getCurrencyRatesFlow(
        baseCurrency: Currency
    ): Flow<NetworkResultWrapper<List<CurrencyWithRate>>> {
        val currencyCode = baseCurrency.currencyCode
        return periodicalFlowFrom(TimeUnit.SECONDS.toMillis(1)) {
            wrapNetworkSuccess(currencyRatesClient.getCurrentRates(currencyCode).toCurrencyWithRatioList())
        }.retryWhen { throwable, _ ->
            val handledValue: NetworkResultWrapper<List<CurrencyWithRate>> =
                throwable.toNetworkResult()
            emit(handledValue)
            true
        }
    }

    private fun <T> wrapNetworkSuccess(value: T): NetworkResultWrapper<T> {
        return NetworkResultWrapper.Success(value)
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

    private fun <T> Throwable.toNetworkResult(): NetworkResultWrapper<T> {
        return when (this) {
            is IOException -> NetworkResultWrapper.NetworkError
            is HttpException -> NetworkResultWrapper.GenericError(code())
            else -> NetworkResultWrapper.GenericError()
        }
    }
}