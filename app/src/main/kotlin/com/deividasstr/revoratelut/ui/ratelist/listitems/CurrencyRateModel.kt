package com.deividasstr.revoratelut.ui.ratelist.listitems

import androidx.annotation.DrawableRes
import com.deividasstr.revoratelut.domain.Currency
import java.math.BigDecimal

data class CurrencyRateModel(
    val currency: Currency,
    val rate: BigDecimal,
    val currencyName: String,
    @DrawableRes val currencyCountryPic: Int
): CurrencyRatesListItem {

    override val id = currency.currencyCode
}