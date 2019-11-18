package com.deividasstr.revoratelut.data.storage

import com.deividasstr.revoratelut.data.storage.db.CurrencyRateDao
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRatio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CurrencyRatesStorageImpl(private val currencyRateDao: CurrencyRateDao) :
    CurrencyRatesStorage {

    override fun getCurrencyRatesFlow(): Flow<List<CurrencyWithRatio>> {
        return currencyRateDao.getAll().map(::entitiesToCurrenciesWithRate)
    }

    override suspend fun setCurrencyRates(currencyRates: List<CurrencyWithRatio>) {
        currencyRateDao.insertAll(currencyRates.map { it.toCurrencyRateEntity() })
    }

    private suspend fun entitiesToCurrenciesWithRate(
        currencyRateEntities: List<CurrencyRateEntity>
    ): List<CurrencyWithRatio> {
        return currencyRateEntities.map { it.toCurrencyWithRate() }
    }

    private fun CurrencyRateEntity.toCurrencyWithRate(): CurrencyWithRatio {
        val currency = Currency(currencyCode)
        val rate = rate.toBigDecimal()
        return CurrencyWithRatio(currency, rate)
    }

    private fun CurrencyWithRatio.toCurrencyRateEntity(): CurrencyRateEntity {
        return CurrencyRateEntity(currency.currencyCode, ratio.toDouble())
    }
}