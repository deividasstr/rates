package com.deividasstr.revoratelut.data.storage.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deividasstr.revoratelut.data.storage.CurrencyRateEntity

@Dao
interface CurrencyRateDao {
    @Query("SELECT * FROM currencyrateentity")
    suspend fun getAll(): List<CurrencyRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencyRates: List<CurrencyRateEntity>)
}