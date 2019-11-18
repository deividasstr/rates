package com.deividasstr.revoratelut.data.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyRateEntity(
    @PrimaryKey val currencyCode: String,
    val rate: Double)