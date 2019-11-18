package com.deividasstr.revoratelut.data.storage

import com.deividasstr.revoratelut.domain.CurrencyWithRatio
import kotlinx.coroutines.flow.Flow

interface CurrencyRatesStorage {

    fun getCurrencyRatesFlow(): Flow<List<CurrencyWithRatio>>
    suspend fun setCurrencyRates(currencyRates: List<CurrencyWithRatio>)
}