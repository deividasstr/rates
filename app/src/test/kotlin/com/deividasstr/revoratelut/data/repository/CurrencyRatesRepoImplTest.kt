package com.deividasstr.revoratelut.data.repository

import TestData
import com.deividasstr.revoratelut.data.network.CurrencyRateNetworkSource
import com.deividasstr.revoratelut.data.network.NetworkResultWrapper
import com.deividasstr.revoratelut.data.storage.CurrencyRatesStorage
import com.deividasstr.revoratelut.data.storage.sharedprefs.SharedPrefs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrencyRatesRepoImplTest {

    private val storage: CurrencyRatesStorage = mockk(relaxUnitFun = true)
    private val networkSource: CurrencyRateNetworkSource = mockk()
    private val sharedPrefs: SharedPrefs = mockk(relaxUnitFun = true)
    private val repo = CurrencyRatesRepoImpl(networkSource, storage, sharedPrefs)

    @Before
    fun setUp() {
        every { sharedPrefs.getLatestBaseCurrency() } returns TestData.eurCurrency
    }

    @Test
    fun `when getting currencyRatesResult and no cache, should retrieve data from network`() = runBlockingTest {
        coEvery { storage.getCurrencyRates() } returns emptyList()

        val expected = NetworkResultWrapper.Success(TestData.rates)

        every { networkSource.getCurrencyRatesFlow(any()) } returns flowOf(expected)

        val result = repo.currencyRatesResultFlow(TestData.eurCurrency).first()

        result shouldEqual CurrencyRatesResult.FreshResult(expected.value)
    }

    @Test
    fun `when getting currencyRatesResult and no cache and retrieved from network, should cache data`() = runBlockingTest {
        coEvery { storage.getCurrencyRates() } returns emptyList()

        val expected = NetworkResultWrapper.Success(TestData.rates)
        every { networkSource.getCurrencyRatesFlow(any()) } returns flowOf(expected)

        repo.currencyRatesResultFlow(TestData.eurCurrency).first()

        coVerify { storage.setCurrencyRates(any()) }
    }

    @Test
    fun `when getting currencyRatesResult and cache available and network request fails, should return cache with reason`() =
        runBlockingTest {
            coEvery { storage.getCurrencyRates() } returns TestData.rates

            val expected = NetworkResultWrapper.GenericError()
            every { networkSource.getCurrencyRatesFlow(any()) } returns flowOf(expected)

            val result = repo.currencyRatesResultFlow(TestData.eurCurrency).first()
            result shouldEqual CurrencyRatesResult.StaleResult(
                TestData.rates,
                RemoteFailure.GenericFailure
            )
        }

    @Test
    fun `when getting currencyRatesResult and no cache and no network, should return empty data and reason`() = runBlockingTest {
        coEvery { storage.getCurrencyRates() } returns emptyList()

        val expected = NetworkResultWrapper.GenericError()
        every { networkSource.getCurrencyRatesFlow(any()) } returns flowOf(expected)

        val result = repo.currencyRatesResultFlow(TestData.eurCurrency).first()
        result shouldEqual CurrencyRatesResult.NoResults(RemoteFailure.GenericFailure)
    }

    @Test
    fun `when getting currencyRatesResult and cache available, should prioritize network data`() = runBlockingTest {
        coEvery { storage.getCurrencyRates() } returns TestData.rates2

        val expected = NetworkResultWrapper.Success(TestData.rates)
        every { networkSource.getCurrencyRatesFlow(any()) } returns flowOf(expected)

        val result = repo.currencyRatesResultFlow(TestData.eurCurrency).first()

        result shouldEqual CurrencyRatesResult.FreshResult(expected.value)
    }
}