package com.deividasstr.revoratelut.data.repository

import com.deividasstr.revoratelut.domain.CurrencyWithRate

sealed class CurrencyRatesResult(open val currencyRates: List<CurrencyWithRate>? = null) {

    data class NoResults(val networkFailure: RemoteFailure) : CurrencyRatesResult()

    data class StaleResult(
        override val currencyRates: List<CurrencyWithRate>,
        val networkFailure: RemoteFailure
    ) : CurrencyRatesResult()

    data class FreshResult(override val currencyRates: List<CurrencyWithRate>) :
        CurrencyRatesResult()

    data class InitialResult(override val currencyRates: List<CurrencyWithRate>) :
        CurrencyRatesResult()

    fun rewrapNewRates(rates: List<CurrencyWithRate>): CurrencyRatesResult {
        return when (this) {
            is StaleResult -> StaleResult(rates, this.networkFailure)
            is FreshResult -> FreshResult(rates)
            is InitialResult -> InitialResult(rates)
            else -> throw IllegalArgumentException("Unknown subclass $this requires rewrap")
        }
    }
}