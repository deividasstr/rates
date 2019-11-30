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
    fun `when no results due to network and no cache, should load and then return proper reason`() {
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
    fun `when no results due to generic issue, should return proper reason`() {
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
    fun `when no issues, should return fresh list of rates`() {
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
    fun `when network issues and cache available, should return stale list of rates with reason`() {
        every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns flowOf(
            CurrencyRatesResult.StaleResult(TestData.ratesEurBase, RemoteFailure.NetworkFailure)
        ).onEach { delay(20) }

        viewModel.currencyRatesLive()
            .test()
            .awaitValue()
            .assertValue(CurrencyRatesState.Loading)
            .awaitNextValue()
            .assertValue(TestData.currencyRatesAvailableStaleNetworkIssue)
    }

    @Test
    fun `when setting new base currency, should cache currency`() {
        viewModel.changeBaseCurrency(TestData.gbpCurrency, TestData.eurRate.toString())

        coVerify { sharedPrefs.setBaseCurrency(TestData.gbpCurrency) }
    }

    @Test
    fun `when setting new base currency and rate value, should return new list of rates`() {
        every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returnsMany listOf(
            flowOf(
                CurrencyRatesResult.FreshResult(TestData.ratesEurBase),
                CurrencyRatesResult.FreshResult(TestData.ratesGbpBase)
            ).onEach { delay(20) } // Too fast otherwise
        )

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
    fun `when setting new base currency, should cache rate`() {
        viewModel.changeBaseCurrency(TestData.gbpCurrency, TestData.eurRate.toString())

        coVerify { sharedPrefs.setBaseCurrencyValue(TestData.eurRate.toBigDecimal()) }
    }

    @Test
    fun `when setting new base currency rate, should cache`() {
        viewModel.changeBaseCurrencyRate(TestData.eurRate.toString())

        coVerify { sharedPrefs.setBaseCurrencyValue(TestData.eurRate.toBigDecimal()) }
    }

    @Test
    fun `when setting new base currency, should set in repo`() {
        viewModel.changeBaseCurrency(TestData.gbpCurrency, TestData.eurRate.toString())

        coVerify { repo.setBaseCurrency(TestData.gbpCurrency) }
    }
}