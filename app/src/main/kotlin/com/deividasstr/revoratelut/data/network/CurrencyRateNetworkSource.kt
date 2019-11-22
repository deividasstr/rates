package com.deividasstr.revoratelut.data.network

import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import kotlinx.coroutines.flow.Flow

interface CurrencyRateNetworkSource {

    fun getCurrencyRatesFlow(baseCurrency: Currency):
        Flow<NetworkResultWrapper<List<CurrencyWithRate>>>
}