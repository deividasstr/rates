package com.deividasstr.revoratelut.data.repository

import com.deividasstr.revoratelut.data.network.CurrencyRateNetworkSource
import com.deividasstr.revoratelut.data.network.NetworkResultWrapper
import com.deividasstr.revoratelut.data.storage.CurrencyRatesStorage
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class CurrencyRatesRepoImpl(
    private val currencyRatesNetworkSource: CurrencyRateNetworkSource,
    private val currencyRatesStorage: CurrencyRatesStorage
) : CurrencyRatesRepo {

    override fun currencyRatesResultFlow(baseCurrency: Currency): Flow<CurrencyRatesResult> {
        return currencyRatesNetworkSource.getCurrencyRatesFlow(baseCurrency)
            .map { tryAddBaseCurrency(it, baseCurrency) }
            .onEach { if (it is NetworkResultWrapper.Success) cacheResult(it.value) }
            .map(::networkCurrenciesToCurrencyRatesModel)
            .onStart {
                val cache = currencyRatesStorage.getCurrencyRates()
                emit(CurrencyRatesResult.InitialResult(cache))
            }
    }

    private fun tryAddBaseCurrency(
        result: NetworkResultWrapper<List<CurrencyWithRate>>,
        baseCurrency: Currency): NetworkResultWrapper<List<CurrencyWithRate>> {
        return when (result) {
            is NetworkResultWrapper.Success -> resultWithBaseCurrency(result, baseCurrency)
            else -> result
        }
    }

    private fun resultWithBaseCurrency(
        result: NetworkResultWrapper.Success<List<CurrencyWithRate>>,
        baseCurrency: Currency
    ): NetworkResultWrapper<List<CurrencyWithRate>> {
        val baseCurrencyWithRate = CurrencyWithRate(baseCurrency, 1.00.toBigDecimal())
        val allCurrencies = listOf(baseCurrencyWithRate).plus(result.value)
        return NetworkResultWrapper.Success(allCurrencies)
    }

    private suspend fun cacheResult(result: List<CurrencyWithRate>) {
        currencyRatesStorage.setCurrencyRates(result)
    }

    private suspend fun networkCurrenciesToCurrencyRatesModel(
        networkResult: NetworkResultWrapper<List<CurrencyWithRate>>
    ): CurrencyRatesResult {
        return when (networkResult) {
            is NetworkResultWrapper.Success -> {
                CurrencyRatesResult.FreshResult(networkResult.value)
            }
            else -> {
                val failure = networkResultFailToRemoteFailure(networkResult)
                val cachedRates = currencyRatesStorage.getCurrencyRates()
                if (cachedRates.isNotEmpty()) {
                    CurrencyRatesResult.StaleResult(cachedRates, failure)
                } else {
                    CurrencyRatesResult.NoResults(failure)
                }
            }
        }
    }

    private fun networkResultFailToRemoteFailure(result: NetworkResultWrapper<*>): RemoteFailure {
        return when (result) {
            is NetworkResultWrapper.GenericError -> RemoteFailure.GenericFailure
            NetworkResultWrapper.NetworkError -> RemoteFailure.NetworkFailure
            else -> throw IllegalArgumentException("Unknown network result failure $result")
        }
    }
}