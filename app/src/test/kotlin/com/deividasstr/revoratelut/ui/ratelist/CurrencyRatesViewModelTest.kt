package com.deividasstr.revoratelut.ui.ratelist

import TestData
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.deividasstr.revoratelut.R
import com.deividasstr.revoratelut.data.repository.CurrencyRatesRepo
import com.deividasstr.revoratelut.data.repository.CurrencyRatesResult
import com.deividasstr.revoratelut.data.repository.RemoteFailure
import com.deividasstr.revoratelut.ui.utils.currency.CurrencyHelper
import com.deividasstr.revoratelut.ui.utils.toArgedText
import com.jraska.livedata.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
    private val viewModel = CurrencyRatesViewModel(repo, currencyHelper)

    @Test
    fun `when no results due to network and no cache, should return proper reason`() {
        every { repo.currencyRatesResultFlow() } returns flowOf(
            CurrencyRatesResult.NoResults(RemoteFailure.NetworkFailure)
        )

        val expected = CurrencyRatesState.Unavailable(R.string.issue_network.toArgedText())
        viewModel.currencyRatesLive()
            .test()
            .awaitValue()
            .assertValue(expected)
    }

    @Test
    fun `when no results due to generic issue, should return proper reason`() {
        every { repo.currencyRatesResultFlow() } returns flowOf(
            CurrencyRatesResult.NoResults(RemoteFailure.GenericFailure)
        )

        val expected = CurrencyRatesState.Unavailable(R.string.issue_generic.toArgedText())
        viewModel.currencyRatesLive()
            .test()
            .awaitValue()
            .assertValue(expected)
    }

    @Test
    fun `when no issues, should return fresh list of rates`() {
        every { repo.currencyRatesResultFlow() } returns flowOf(
            CurrencyRatesResult.FreshResult(TestData.rates)
        )

        viewModel.currencyRatesLive()
            .test()
            .awaitValue()
            .assertValue(TestData.currencyRatesAvailableFresh)
    }

    @Test
    fun `when network issues and cache available, should return stale list of rates with reason`() {
        every { repo.currencyRatesResultFlow() } returns flowOf(
            CurrencyRatesResult.StaleResult(TestData.rates, RemoteFailure.NetworkFailure)
        )

        viewModel.currencyRatesLive()
            .test()
            .awaitValue()
            .assertValue(TestData.currencyRatesAvailableStale)
    }
}