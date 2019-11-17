package com.deividasstr.revoratelut.data.network

import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRatio
import kotlinx.coroutines.flow.Flow

interface CurrencyRateNetworkSource {

    fun getCurrencyToRateFlow(baseCurrency: Currency): Flow<List<CurrencyWithRatio>>
}