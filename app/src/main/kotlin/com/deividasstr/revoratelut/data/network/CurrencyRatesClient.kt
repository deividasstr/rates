package com.deividasstr.revoratelut.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRatesClient {

    @GET("latest")
    suspend fun getCurrentRates(@Query("base") baseCurrency: String): CurrencyRatesResponse
}