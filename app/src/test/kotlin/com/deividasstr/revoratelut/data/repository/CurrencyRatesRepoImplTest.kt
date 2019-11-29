package com.deividasstr.revoratelut.data.repository

import TestData
import com.deividasstr.revoratelut.data.network.CurrencyRateNetworkSource
import com.deividasstr.revoratelut.data.network.NetworkResultWrapper
import com.deividasstr.revoratelut.data.storage.CurrencyRatesStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldContainSame
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrencyRatesRepoImplTest {

    private val storage: CurrencyRatesStorage = mockk(relaxUnitFun = true)
    private val networkSource: CurrencyRateNetworkSource = mockk()
    private val repo = CurrencyRatesRepoImpl(networkSource, storage)

    @Test
    fun `when getting currencyRatesResult and no cache, should return initial empty data and return data from network`() =
        runBlockingTest {
            coEvery { storage.getCurrencyRates() } returns emptyList()

            val answer = NetworkResultWrapper.Success(TestData.ratesWOBaseEur)

            every { networkSource.getCurrencyRatesFlow(any()) } returns flowOf(answer)

            val result = repo.currencyRatesResultFlow(TestData.eurCurrency)
                .take(2)
                .toList(mutableListOf())

            val answerWEur = NetworkResultWrapper.Success(TestData.rates)

            val expected = listOf(
                CurrencyRatesResult.InitialResult(emptyList()),
                CurrencyRatesResult.FreshResult(answerWEur.value)
            )

            result shouldContainSame expected
        }

    @Test
    fun `when getting currencyRatesResult and no cache and retrieved from network, should cache data`() =
        runBlockingTest {
            coEvery { storage.getCurrencyRates() } returns emptyList()

            val answer = NetworkResultWrapper.Success(TestData.rates)
            every { networkSource.getCurrencyRatesFlow(any()) } returns flowOf(answer)

            repo.currencyRatesResultFlow(TestData.eurCurrency).take(2).count()

            coVerify { storage.setCurrencyRates(any()) }
        }

    @Test
    fun `when getting currencyRatesResult and cache available and network request fails, should return cache with reason`() =
        runBlockingTest {
            coEvery { storage.getCurrencyRates() } returns TestData.rates

            val answer = NetworkResultWrapper.GenericError()
            every { networkSource.getCurrencyRatesFlow(any()) } returns flowOf(answer)

            val result = repo.currencyRatesResultFlow(TestData.eurCurrency)
                .take(2)
                .toList(mutableListOf())

            val expected = listOf(
                CurrencyRatesResult.InitialResult(TestData.rates),
                CurrencyRatesResult.StaleResult(
                    TestData.rates,
                    RemoteFailure.GenericFailure
                )
            )

            result shouldContainSame expected
        }

    @Test
    fun `when getting currencyRatesResult and no cache and no network, should return empty data and reason`() =
        runBlockingTest {
            coEvery { storage.getCurrencyRates() } returns emptyList()

            val answer = NetworkResultWrapper.GenericError()
            every { networkSource.getCurrencyRatesFlow(any()) } returns flowOf(answer)

            val result = repo.currencyRatesResultFlow(TestData.eurCurrency)
                .take(2)
                .toList(mutableListOf())

            val expected = listOf(
                CurrencyRatesResult.InitialResult(emptyList()),
                CurrencyRatesResult.NoResults(RemoteFailure.GenericFailure)
            )

            result shouldContainSame expected
        }

    @Test
    fun `when getting currencyRatesResult and cache available, should return cache on initially and then network data`() =
        runBlockingTest {
            coEvery { storage.getCurrencyRates() } returns TestData.rates2

            val answer = NetworkResultWrapper.Success(TestData.ratesWOBaseEur)
            every { networkSource.getCurrencyRatesFlow(any()) } returns flowOf(answer)

            val result = repo.currencyRatesResultFlow(TestData.eurCurrency)
                .take(2)
                .toList(mutableListOf())

            val answerWEur = NetworkResultWrapper.Success(TestData.rates)

            val expected = listOf(
                CurrencyRatesResult.InitialResult(TestData.rates2),
                CurrencyRatesResult.FreshResult(answerWEur.value)
            )

            result shouldContainSame expected
        }
}