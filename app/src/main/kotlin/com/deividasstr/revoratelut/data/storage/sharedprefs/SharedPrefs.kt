package com.deividasstr.revoratelut.data.storage.sharedprefs

import com.deividasstr.revoratelut.domain.Currency

interface SharedPrefs {

    fun getLatestBaseCurrency(): Currency
    fun setLatestBaseCurrency(currency: Currency)
}