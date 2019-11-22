package com.deividasstr.revoratelut.data.storage.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import com.deividasstr.revoratelut.domain.Currency

class SharedPrefsImpl(private val context: Context) : SharedPrefs {

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(CURRENCY_PREFS, Context.MODE_PRIVATE)
    }

    private var latestBaseCurrency: String
        get() =  prefs.getString(BASE_CURRENCY, DEFAULT_CURRENCY)!!
        set(value) = prefs.edit().putString(BASE_CURRENCY, value).apply()

    override fun getLatestBaseCurrency() = Currency(latestBaseCurrency)

    override fun setLatestBaseCurrency(currency: Currency) {
        latestBaseCurrency = currency.currencyCode
    }

    companion object {
        const val DEFAULT_CURRENCY = "EUR"
        const val CURRENCY_PREFS = "CURRENCY_PREFS"
        const val BASE_CURRENCY = "BASE_CURRENCY"
    }
}