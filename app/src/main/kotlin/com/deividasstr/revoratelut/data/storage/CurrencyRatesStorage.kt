package com.deividasstr.revoratelut.data.storage

import com.deividasstr.revoratelut.domain.CurrencyWithRate
import kotlinx.coroutines.flow.Flow

interface CurrencyRatesStorage {

    fun getCurrencyRatesFlow(): Flow<List<CurrencyWithRate>>
    suspend fun setCurrencyRates(currencyRates: List<CurrencyWithRate>)
}