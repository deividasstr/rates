package com.deividasstr.revoratelut.data.storage.sharedprefs

import com.deividasstr.revoratelut.domain.Currency
import java.math.BigDecimal

interface SharedPrefs {

    fun baseCurrency(): Currency
    suspend fun setBaseCurrency(currency: Currency)

    fun baseCurrencyValue(): BigDecimal
    suspend fun setBaseCurrencyValue(value: BigDecimal)
}