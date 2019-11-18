package com.deividasstr.revoratelut.data.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.deividasstr.revoratelut.data.storage.CurrencyRateEntity

@Database(entities = [CurrencyRateEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun currencyRateDao(): CurrencyRateDao
}