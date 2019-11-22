package com.deividasstr.revoratelut.data.storage

import com.deividasstr.revoratelut.domain.CurrencyWithRate

interface CurrencyRatesStorage {

    suspend fun getCurrencyRates(): List<CurrencyWithRate>
    suspend fun setCurrencyRates(currencyRates: List<CurrencyWithRate>)
}