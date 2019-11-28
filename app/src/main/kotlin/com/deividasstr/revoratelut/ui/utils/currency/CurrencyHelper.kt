package com.deividasstr.revoratelut.ui.utils.currency

import com.mynameismidori.currencypicker.ExtendedCurrency

class CurrencyHelper {

    private val currencyCodeToCurrencyDetails = HashMap<String, CurrencyDetails>()

    fun getCurrencyDetails(currencyCode: String): CurrencyDetails {
        return currencyCodeToCurrencyDetails.getOrPut(currencyCode) {
            val detailedCurrency =
                ExtendedCurrency.getAllCurrencies().find { it.code == currencyCode }
                    ?: throw IllegalArgumentException("Not known currency $currencyCode")

            CurrencyDetails(detailedCurrency.name, detailedCurrency.flag)
        }
    }
}
