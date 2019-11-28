package com.deividasstr.revoratelut.ui.ratelist.listitems

import com.deividasstr.revoratelut.ui.utils.ArgedText
import com.deividasstr.revoratelut.ui.utils.delegating.ListItem

data class CurrencyRatesListHint(val text: ArgedText): ListItem {

    override val id = text.hashCode().toString()
}