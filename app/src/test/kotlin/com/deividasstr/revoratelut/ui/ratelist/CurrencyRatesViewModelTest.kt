package com.deividasstr.revoratelut.ui.ratelist

import TestData
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.deividasstr.revoratelut.data.repository.CurrencyRatesRepo
import com.deividasstr.revoratelut.data.repository.CurrencyRatesResult
import com.deividasstr.revoratelut.data.repository.RemoteFailure
import com.deividasstr.revoratelut.data.storage.sharedprefs.SharedPrefs
import com.deividasstr.revoratelut.domain.NumberFormatter
import com.deividasstr.revoratelut.ui.utils.currency.CurrencyHelper
import com.jraska.livedata.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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

    private val repo = mockk<CurrencyRatesRepo>()
    private val currencyHelper = CurrencyHelper()
    private val sharedPrefs = mockk<SharedPrefs>(relaxUnitFun = true)
    private val formatter = NumberFormatter()
    private val viewModel = CurrencyRatesViewModel(repo, currencyHelper, sharedPrefs, formatter)

    @Before
    fun setUp() {
        every { sharedPrefs.getLatestBaseCurrency() } returns TestData.eurCurrency
    }

    @Test
    fun `when no results due to network and no cache, should load and then return proper reason`() {
        every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns flowOf(
            CurrencyRatesResult.NoResults(RemoteFailure.NetworkFailure)
        )

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
        )

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
            CurrencyRatesResult.FreshResult(TestData.rates)
        )

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
            CurrencyRatesResult.StaleResult(TestData.rates, RemoteFailure.NetworkFailure)
        )

        viewModel.currencyRatesLive()
            .test()
            .awaitValue()
            .assertValue(CurrencyRatesState.Loading)
            .awaitNextValue()
            .assertValue(TestData.currencyRatesAvailableStaleNetworkIssue)
    }

    @Test
    fun `when setting new base currency, should cache`() {
        viewModel.focusedCurrency(TestData.gbpCurrency)

        verify { sharedPrefs.setLatestBaseCurrency(TestData.gbpCurrency) }
    }

    @Test
    fun `when setting new base currency, should return new list of rates`() {
        every { repo.currencyRatesResultFlow(TestData.eurCurrency) } returns flowOf(
            CurrencyRatesResult.FreshResult(TestData.rates)
        )

        every { repo.currencyRatesResultFlow(TestData.gbpCurrency) } returns flowOf(
            CurrencyRatesResult.FreshResult(TestData.rates2)
        )

        val currencyRatesLive = viewModel.currencyRatesLive()
            .test()
            .awaitValue()
            .assertValue(CurrencyRatesState.Loading)
            .awaitNextValue()
            .assertValue(TestData.currencyRatesAvailableFresh)

        viewModel.focusedCurrency(TestData.gbpCurrency)

        currencyRatesLive.awaitNextValue()
            .assertValue(TestData.currencyRatesAvailableFresh2)
    }
}