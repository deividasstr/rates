package com.deividasstr.revoratelut.data.storage

import com.deividasstr.revoratelut.data.storage.db.CurrencyRateDao
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRate

class CurrencyRatesStorageImpl(private val currencyRateDao: CurrencyRateDao) :
    CurrencyRatesStorage {

    override suspend fun getCurrencyRates(): List<CurrencyWithRate> {
        return currencyRateDao.getAll().map { it.toCurrencyWithRate() }
    }

    override suspend fun setCurrencyRates(currencyRates: List<CurrencyWithRate>) {
        currencyRateDao.insertAll(currencyRates.map { it.toCurrencyRateEntity() })
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