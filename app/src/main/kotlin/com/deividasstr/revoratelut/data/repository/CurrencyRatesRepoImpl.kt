package com.deividasstr.revoratelut.data.repository

import com.deividasstr.revoratelut.data.network.CurrencyRateNetworkSource
import com.deividasstr.revoratelut.data.network.NetworkResultWrapper
import com.deividasstr.revoratelut.data.storage.CurrencyRatesStorage
import com.deividasstr.revoratelut.data.storage.sharedprefs.SharedPrefs
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class CurrencyRatesRepoImpl(
    private val currencyRatesNetworkSource: CurrencyRateNetworkSource,
    private val currencyRatesStorage: CurrencyRatesStorage,
    private val sharedPrefs: SharedPrefs
) : CurrencyRatesRepo {

    override fun currencyRatesResultFlow(baseCurrency: Currency): Flow<CurrencyRatesResult> {
        return currencyRatesNetworkSource.getCurrencyRatesFlow(baseCurrency)
            .onEach {
                if (it is NetworkResultWrapper.Success) {
                    cacheLatestCurrencyRates(it, baseCurrency)
                }
            }
            .map(::networkCurrenciesToCurrencyRatesModel)
    }

    private suspend fun cacheLatestCurrencyRates(
        result: NetworkResultWrapper.Success<List<CurrencyWithRate>>,
        baseCurrency: Currency
    ) {
        currencyRatesStorage.setCurrencyRates(result.value)
        sharedPrefs.setLatestBaseCurrency(baseCurrency)
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