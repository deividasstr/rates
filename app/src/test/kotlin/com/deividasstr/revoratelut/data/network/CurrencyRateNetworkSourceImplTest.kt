package com.deividasstr.revoratelut.data.network

import TestData
import com.deividasstr.revoratelut.domain.CurrencyWithRate
import io.mockk.ConstantAnswer
import io.mockk.ManyAnswersAnswer
import io.mockk.ThrowingAnswer
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.test.runBlockingTest
import org.amshove.kluent.shouldContainSame
import org.junit.Test
import java.io.IOException

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

        rateNetworkSource.getCurrencyRatesFlow(TestData.eurCurrency)
            .take(2)
            .toCollection(results)

        results shouldContainSame listOf(
            NetworkResultWrapper.Success(TestData.rates),
            NetworkResultWrapper.Success(TestData.rates2)
        )
    }

    @Test
    fun `when observing response and failure, should return appropriate responses`() =
        runBlockingTest {
            val responseList = listOf(
                ConstantAnswer(TestData.response),
                ThrowingAnswer(IOException())
            )

            coEvery { currencyRatesClient.getCurrentRates(any()) } answers ManyAnswersAnswer(
                responseList)

            val results = mutableListOf<NetworkResultWrapper<List<CurrencyWithRate>>>()

            rateNetworkSource.getCurrencyRatesFlow(TestData.eurCurrency)
                .take(2)
                .toCollection(results)

            results shouldContainSame listOf(
                NetworkResultWrapper.Success(TestData.rates),
                NetworkResultWrapper.NetworkError
            )
        }

    @Test
    fun `when failure happens, should not stop retrying`() =
        runBlockingTest {
            val responseList = listOf(
                ThrowingAnswer(IOException()),
                ConstantAnswer(TestData.response2)
            )

            coEvery { currencyRatesClient.getCurrentRates(any()) } answers
                ManyAnswersAnswer(responseList)

            val results = mutableListOf<NetworkResultWrapper<List<CurrencyWithRate>>>()

            rateNetworkSource.getCurrencyRatesFlow(TestData.eurCurrency)
                .take(2)
                .toCollection(results)

            results shouldContainSame listOf(
                NetworkResultWrapper.NetworkError,
                NetworkResultWrapper.Success(TestData.rates2)
            )
        }
}
