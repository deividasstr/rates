package com.deividasstr.revoratelut.ui.ratelist

import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRateModel
import com.deividasstr.revoratelut.ui.ratelist.listitems.CurrencyRatesListHint

sealed class CurrencyRatesState {

    object Loading : CurrencyRatesState()

    data class Loaded(
        val rates: List<CurrencyRateModel>? = null,
        val hint: CurrencyRatesListHint? = null
    ) : CurrencyRatesState()
}