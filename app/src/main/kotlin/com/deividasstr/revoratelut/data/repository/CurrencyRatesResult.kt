package com.deividasstr.revoratelut.data.repository

import com.deividasstr.revoratelut.domain.CurrencyWithRate

sealed class CurrencyRatesResult {

    data class NoResults(val networkFailure: RemoteFailure) : CurrencyRatesResult()

    data class StaleResult(
        val currencyRates: List<CurrencyWithRate>,
        val networkFailure: RemoteFailure
    ) : CurrencyRatesResult()

    data class FreshResult(val currencyRates: List<CurrencyWithRate>) : CurrencyRatesResult()

    data class InitialResult(val currencyRates: List<CurrencyWithRate>) : CurrencyRatesResult()
}