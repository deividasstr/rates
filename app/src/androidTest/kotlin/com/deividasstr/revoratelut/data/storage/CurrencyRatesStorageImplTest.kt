package com.deividasstr.revoratelut.data.storage

import TestData
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.deividasstr.revoratelut.data.storage.db.AppDb
import com.deividasstr.revoratelut.data.storage.db.CurrencyRateDao
import com.deividasstr.revoratelut.utils.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldHaveSize
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class CurrencyRatesStorageImplTest {

    // Small dilemma here - shall we include the testing of the db and make it an integration
    // test? Then we need to move this test ot androidTest. But it is easier to TDD everything
    // before actually launching the app and checking that db parsing is valid

    @get:Rule
    val testCoroutine = TestCoroutineRule()

    private lateinit var currencyRatesStorage: CurrencyRatesStorage
    private lateinit var currencyRateDao: CurrencyRateDao
    private lateinit var db: AppDb

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room
            .inMemoryDatabaseBuilder(context, AppDb::class.java)
            .setQueryExecutor(testCoroutine.testDispatcher.asExecutor())
            .setTransactionExecutor(testCoroutine.testDispatcher.asExecutor())
            .build()

        currencyRateDao = db.currencyRateDao()
        currencyRatesStorage = CurrencyRatesStorageImpl(currencyRateDao)
    }

    @Test
    fun whenDbIsEmpty_getAllReturnsEmptyList() = testCoroutine.testDispatcher.runBlockingTest {
        val result = currencyRatesStorage.getCurrencyRatesFlow().first()
        result shouldHaveSize 0
    }

    @Test
    fun whenAddCurrencyRatings_getAllReturnsCurrencyRatings() =
        testCoroutine.testDispatcher.runBlockingTest {
            currencyRatesStorage.setCurrencyRates(TestData.rates)
            val result = currencyRatesStorage.getCurrencyRatesFlow().first()
            result shouldContainSame TestData.rates
        }

    @Test
    fun whenNewCurrencyRatesSet_shouldOverrideOldOnes() =
        testCoroutine.testDispatcher.runBlockingTest {
            currencyRatesStorage.setCurrencyRates(TestData.rates)
            val result = currencyRatesStorage.getCurrencyRatesFlow().first()
            result shouldContainSame TestData.rates

            currencyRatesStorage.setCurrencyRates(TestData.rates2)
            val result2 = currencyRatesStorage.getCurrencyRatesFlow().first()
            result2 shouldContainSame TestData.rates2
        }

    @After
    fun tearDown() {
        db.close()
    }
}