package com.deividasstr.revoratelut.data.network

import TestData
import com.deividasstr.revoratelut.domain.Currency
import com.deividasstr.revoratelut.domain.CurrencyWithRatio
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withTimeoutOrNull
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqualTo
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class CurrencyRateNetworkSourceImplTest {

    private val currencyRatesClient = mockk<CurrencyRatesClient>()
    private val rateNetworkSource = CurrencyRateNetworkSourceImpl(currencyRatesClient)

    @Test
    fun `when observing for 2 responses, should return correct`() = runBlockingTest {
        val responseList = listOf(TestData.response, TestData.response2)
        coEvery { currencyRatesClient.getCurrentRates(any()) } returnsMany responseList

        val results = mutableListOf<List<CurrencyWithRatio>>()

        rateNetworkSource.getCurrencyToRateFlow(Currency("EUR"))
            .take(2)
            .toCollection(results)

        results shouldContainSame listOf(TestData.rates, TestData.rates2)
    }

    @Test
    fun `when observing between 1 - 2 seconds, should return 2 responses`() = runBlockingTest {
        coEvery { currencyRatesClient.getCurrentRates(any()) } returns TestData.response

        val results = mutableListOf<List<CurrencyWithRatio>>()

        withTimeoutOrNull(1100) {
            rateNetworkSource.getCurrencyToRateFlow(Currency("EUR"))
                .toCollection(results)
        }

        results.size shouldEqualTo 2
    }
}