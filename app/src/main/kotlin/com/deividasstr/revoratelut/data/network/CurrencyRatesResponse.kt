package com.deividasstr.revoratelut.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyRatesResponse(val base: String, val date: String, val rates: Map<String, String>)