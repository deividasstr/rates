package com.deividasstr.revoratelut.ui.ratelist

import TestData
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.deividasstr.revoratelut.data.repository.CurrencyRatesRepo
import com.deividasstr.revoratelut.data.repository.CurrencyRatesResult
import com.deividasstr.revoratelut.data.repository.RemoteFailure
import com.deividasstr.revoratelut.data.storage.sharedprefs.SharedPrefs
import com.deividasstr.revoratelut.domain.Calculator
import com.deividasstr.revoratelut.domain.NumberFormatter
import com.deividasstr.revoratelut.ui.utils.currency.CurrencyHelper
import com.jraska.livedata.test
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import utils.TestCoroutineRule

@ExperimentalCoroutinesApi
class CurrencyRatesViewModelTest {

    @get:Rule
    val instantLiveData = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutine = TestCoroutineRule()

    private val repo = mockk<CurrencyRatesRepo>(relaxUnitFun = true)
    private val currencyHelper = CurrencyHelper()
    private val sharedPrefs = mockk<SharedPrefs>(relaxUnitFun = true)
    private val formatter = NumberFormatter()
    private val viewModel =
        CurrencyRatesViewModel(repo, currencyHelper, sharedPrefs, formatter, Calculator())

    @Before
    fun setUp() {
        every { sharedPrefs.baseCurrency() } returns TestData.eurCurrency
        every { sharedPrefs.baseCurrencyValue() } returns TestData.eurRate.toBigDecimal()
    }

    @Test
    fun `when no results due to network and no cache, should load and then return proper reason`() =
        runBlockingTest {
            every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns flowOf(
                CurrencyRatesResult.NoResults(RemoteFailure.NetworkFailure)
            ).onEach { delay(20) }

            val expected = TestData.currencyRatesNotAvailableNetworkIssue
            viewModel.currencyRatesLive()
                .test()
                .awaitValue()
                .assertValue(CurrencyRatesState.Loading)
                .awaitNextValue()
                .assertValue(expected)
        }

    @Test
    fun `when no results due to generic issue, should return proper reason`() = runBlockingTest {
        every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns flowOf(
            CurrencyRatesResult.NoResults(RemoteFailure.GenericFailure)
        ).onEach { delay(20) }

        val expected = TestData.currencyRatesNotAvailableGenericIssue
        viewModel.currencyRatesLive()
            .test()
            .awaitValue()
            .assertValue(CurrencyRatesState.Loading)
            .awaitNextValue()
            .assertValue(expected)
    }

    @Test
    fun `when no issues, should return fresh list of rates`() = runBlockingTest {
        every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns flowOf(
            CurrencyRatesResult.FreshResult(TestData.ratesEurBase)
        ).onEach { delay(20) }

        viewModel.currencyRatesLive()
            .test()
            .awaitValue()
            .assertValue(CurrencyRatesState.Loading)
            .awaitNextValue()
            .assertValue(TestData.currencyRatesAvailableFresh)
    }

    @Test
    fun `when network issues and cache available, should return stale list of rates with reason`() =
        runBlockingTest {
            every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns flowOf(
                CurrencyRatesResult.StaleResult(TestData.ratesEurBase, RemoteFailure.NetworkFailure)
            ).onEach { delay(20) } // Due to high speed LOADING state might not appear

            viewModel.currencyRatesLive()
                .test()
                .awaitValue()
                .assertValue(CurrencyRatesState.Loading)
                .awaitNextValue()
                .assertValue(TestData.currencyRatesAvailableStaleNetworkIssue)
        }

    @Test
    fun `when setting new base currency, should cache currency`() = runBlockingTest {
        viewModel.changeBaseCurrency(TestData.gbpCurrency, TestData.eurRate.toString())

        coVerify { sharedPrefs.setBaseCurrency(TestData.gbpCurrency) }
    }

    @Test
    fun `when setting new base currency and rate value, should return new list of rates`() =
        runBlockingTest {
            every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns
                flowOf(CurrencyRatesResult.FreshResult(TestData.ratesEurBase))
                    .onEach { delay(20) } // Due to high speed LOADING state might not appear

            every { repo.currencyRatesResultFlow(TestData.gbpCurrency) } returns
                flowOf(CurrencyRatesResult.FreshResult(TestData.ratesGbpBase))
            val currencyRatesLive = viewModel.currencyRatesLive()
                .test()
                .awaitValue()
                .assertValue(CurrencyRatesState.Loading)
                .awaitNextValue()
                .assertValue(TestData.currencyRatesAvailableFresh)

            viewModel.changeBaseCurrency(TestData.gbpCurrency, TestData.currencyInputValue)

            currencyRatesLive.awaitNextValue()
                .assertValue(TestData.currencyRatesAvailableFresh2)
        }

    @Test
    fun `when setting new base rate value, should return list with updated rates`() =
        runBlockingTest {
            every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns
                flowOf(CurrencyRatesResult.FreshResult(TestData.ratesEurBase))
                    .onEach { delay(20) } // Due to high speed LOADING state might not appear

            val currencyRatesLive = viewModel.currencyRatesLive()
                .test()
                .awaitValue()
                .assertValue(CurrencyRatesState.Loading)
                .awaitNextValue()
                .assertValue(TestData.currencyRatesAvailableFresh)

            viewModel.changeBaseCurrencyRate(TestData.currencyInputValue)

            currencyRatesLive.awaitNextValue()
                .assertValue(TestData.currencyRatesAvailableFreshAdjusted(TestData.currencyInputValue))
        }

    @Test
    fun `when setting new base currency, should cache rate`() = runBlockingTest {
        viewModel.changeBaseCurrency(TestData.gbpCurrency, TestData.eurRate.toString())

        coVerify { sharedPrefs.setBaseCurrencyValue(TestData.eurRate.toBigDecimal()) }
    }

    //TODO: investigate the reason of test flakiness (not the same dispatcher?)
    @Test
    fun `when setting new base currency rate, should cache`() {
        viewModel.changeBaseCurrencyRate(TestData.eurRate.toString())

        coVerify { sharedPrefs.setBaseCurrencyValue(TestData.eurRate.toBigDecimal()) }
    }

    @Test
    fun `when stale data and setting new base currency and rate value, should return updated list of rates`() =
        testCoroutine.testDispatcher.runBlockingTest {
            every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns
                flowOf(
                    CurrencyRatesResult.StaleResult(
                        TestData.ratesEurBase,
                        RemoteFailure.NetworkFailure)
                ).onEach { delay(20) } // Due to high speed LOADING state might not appear

            every { repo.currencyRatesResultFlow(TestData.gbpCurrency) } returns
                flowOf(
                    CurrencyRatesResult.StaleResult(
                        TestData.ratesGbpBase,
                        RemoteFailure.NetworkFailure)
                )

            val currencyRatesLive = viewModel.currencyRatesLive()
                .test()
                .awaitValue()
                .assertValue(CurrencyRatesState.Loading)
                .awaitNextValue()
                .assertValue(TestData.currencyRatesAvailableStaleNetworkIssue)

            viewModel.changeBaseCurrency(TestData.gbpCurrency, TestData.currencyInputValue)

            currencyRatesLive.awaitNextValue()
                .assertValue(TestData.currencyRatesAvailableStaleNetworkIssue2)
        }

    @Test
    fun `when stale data and setting new base rate value, should return updated list of rates`() {
            every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns
                flowOf(
                    CurrencyRatesResult.StaleResult(
                        TestData.ratesEurBase,
                        RemoteFailure.NetworkFailure)
                ).onEach { delay(20) } // Due to high speed LOADING state might not appear

            val currencyRatesLive = viewModel.currencyRatesLive()
                .test()
                .awaitValue()
                .assertValue(CurrencyRatesState.Loading)
                .awaitNextValue()
                .assertValue(TestData.currencyRatesAvailableStaleNetworkIssue)

            viewModel.changeBaseCurrencyRate(TestData.currencyInputValue)

            currencyRatesLive.awaitNextValue()
                .assertValue(
                    TestData.currencyRatesAvailableStaleNetworkIssueAdjusted(
                        TestData.currencyInputValue
                    ))
        }
}