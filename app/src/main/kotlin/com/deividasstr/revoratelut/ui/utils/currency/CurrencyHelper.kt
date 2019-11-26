package com.deividasstr.revoratelut.ui.utils.currency

import com.mynameismidori.currencypicker.ExtendedCurrency

class CurrencyHelper {

    fun getCurrencyDetails(currencyCode: String): CurrencyDetails {
        val detailedCurrency =
            ExtendedCurrency.getAllCurrencies().find { it.code == currencyCode }
                ?: throw IllegalArgumentException("Not known currency $currencyCode")

        return CurrencyDetails(detailedCurrency.name, detailedCurrency.flag)
    }
}
