package com.deividasstr.revoratelut.data.repository

import com.deividasstr.revoratelut.domain.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRatesRepo {

    fun currencyRatesResultFlow(baseCurrency: Currency): Flow<CurrencyRatesResult>
    suspend fun setBaseCurrency(baseCurrency: Currency)
}