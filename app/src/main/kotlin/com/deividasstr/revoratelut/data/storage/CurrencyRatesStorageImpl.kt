package com.deividasstr.revoratelut.data.storage

import com.deividasstr.revoratelut.data.storage.db.CurrencyRateDao
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CurrencyRatesStorageImpl(private val currencyRateDao: CurrencyRateDao) :
    CurrencyRatesStorage {

    override fun getCurrencyRatesFlow(): Flow<List<CurrencyWithRate>> {
        return currencyRateDao.getAll().map(::entitiesToCurrenciesWithRate)
    }

    override suspend fun setCurrencyRates(currencyRates: List<CurrencyWithRate>) {
        currencyRateDao.insertAll(currencyRates.map { it.toCurrencyRateEntity() })
    }

    private suspend fun entitiesToCurrenciesWithRate(
        currencyRateEntities: List<CurrencyRateEntity>
    ): List<CurrencyWithRate> {
        return currencyRateEntities.map { it.toCurrencyWithRate() }
    }

    private fun CurrencyRateEntity.toCurrencyWithRate(): CurrencyWithRate {
        val currency = Currency(currencyCode)
        val rate = rate.toBigDecimal()
        return CurrencyWithRate(currency, rate)
    }

    private fun CurrencyWithRate.toCurrencyRateEntity(): CurrencyRateEntity {
        return CurrencyRateEntity(currency.currencyCode, rate.toDouble())
    }
}