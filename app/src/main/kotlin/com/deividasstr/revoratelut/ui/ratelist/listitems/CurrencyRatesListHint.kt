package com.deividasstr.revoratelut.ui.ratelist.listitems

import com.deividasstr.revoratelut.ui.utils.ArgedText

data class CurrencyRatesListHint(val text: ArgedText): CurrencyRatesListItem {

    override val id = text.hashCode().toString()
}