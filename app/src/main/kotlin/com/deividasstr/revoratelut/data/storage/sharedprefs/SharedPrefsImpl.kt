package com.deividasstr.revoratelut.data.storage.sharedprefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.deividasstr.revoratelut.domain.Currency
import java.math.BigDecimal

class SharedPrefsImpl(private val context: Context) : SharedPrefs {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(CURRENCY_PREFS, Context.MODE_PRIVATE)
    }

    private var baseCurrency: String
        get() = prefs.getString(BASE_CURRENCY, DEFAULT_CURRENCY)!!
        @SuppressLint("ApplySharedPref")
        set(value) { prefs.edit().putString(BASE_CURRENCY, value).commit() }

    private var baseCurrencyVal: BigDecimal
        get() = prefs.getString(BASE_CURRENCY_VALUE, DEFAULT_CURRENCY_VALUE)!!.toBigDecimal()
        @SuppressLint("ApplySharedPref")
        set(value) { prefs.edit().putString(BASE_CURRENCY_VALUE, value.toString()).commit() }

    override fun baseCurrency() = Currency(baseCurrency)

    override suspend fun setBaseCurrency(currency: Currency) {
        baseCurrency = currency.currencyCode
    }

    override fun baseCurrencyValue() = baseCurrencyVal

    override suspend fun setBaseCurrencyValue(value: BigDecimal) {
        baseCurrencyVal = value
    }

    companion object {
        const val DEFAULT_CURRENCY = "EUR"
        const val DEFAULT_CURRENCY_VALUE = "1.00"
        const val CURRENCY_PREFS = "CURRENCY_PREFS"
        const val BASE_CURRENCY = "BASE_CURRENCY"
        const val BASE_CURRENCY_VALUE = "BASE_CURRENCY_VALUE"
    }
}