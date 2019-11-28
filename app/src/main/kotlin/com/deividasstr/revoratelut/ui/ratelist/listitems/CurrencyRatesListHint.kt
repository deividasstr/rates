package com.deividasstr.revoratelut.ui.ratelist.listitems

import androidx.annotation.DrawableRes
import com.deividasstr.revoratelut.ui.utils.ArgedText
import com.deividasstr.revoratelut.ui.utils.delegating.ListItem

data class CurrencyRatesListHint(
    val firstText: ArgedText,
    val secondText: ArgedText,
    @DrawableRes val icon: Int
): ListItem {

    override val id = hashCode().toString()
}