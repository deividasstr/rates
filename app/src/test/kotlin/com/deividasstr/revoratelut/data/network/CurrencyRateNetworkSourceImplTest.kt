package com.deividasstr.revoratelut.data.network

import TestData
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldContainSame
import org.junit.Test

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class CurrencyRateNetworkSourceImplTest {

    private val currencyRatesClient = mockk<CurrencyRatesClient>()
    private val rateNetworkSource = CurrencyRateNetworkSourceImpl(currencyRatesClient)

    @Test
    fun `when observing for 2 distinct responses, should return 2 responses`() = runBlockingTest {
        val responseList = listOf(TestData.response, TestData.response2)
        coEvery { currencyRatesClient.getCurrentRates(any()) } returnsMany responseList

        val results = mutableListOf<NetworkResultWrapper<List<CurrencyWithRate>>>()

        rateNetworkSource.getCurrencyRateFlow(TestData.eurCurrency)
            .take(2)
            .toCollection(results)

        results shouldContainSame listOf(
            NetworkResultWrapper.Success(TestData.rates),
            NetworkResultWrapper.Success(TestData.rates2)
        )
    }
}