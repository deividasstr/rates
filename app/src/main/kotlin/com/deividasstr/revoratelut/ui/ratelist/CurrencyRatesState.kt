package com.deividasstr.revoratelut.ui.ratelist

import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRateModel
import com.deividasstr.revoratelut.ui.utils.ArgedText

sealed class CurrencyRatesState {

    data class Available(
        val rates: List<CurrencyRateModel>,
        val stale: Boolean,
        val error: ArgedText? = null
    ) : CurrencyRatesState()

    data class Unavailable(val error: ArgedText) : CurrencyRatesState()
}