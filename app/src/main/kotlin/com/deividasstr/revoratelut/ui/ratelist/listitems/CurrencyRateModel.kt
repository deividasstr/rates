package com.deividasstr.revoratelut.ui.ratelist.listitems

import androidx.annotation.DrawableRes
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.ui.utils.delegating.ListItem
import java.math.BigDecimal

data class CurrencyRateModel(
    val currency: Currency,
    val rate: BigDecimal,
    val currencyName: String,
    @DrawableRes val currencyCountryPic: Int
): ListItem {

    override val id = currency.currencyCode

    // If this method was called, old and new items are different
    override fun <T : ListItem> calculatePayload(oldItem: T): Any? {
        if (oldItem is CurrencyRateModel && rate != oldItem.rate) return true
        return null
    }
}