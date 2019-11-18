package com.deividasstr.revoratelut.data.network

import CurrencyRatesTestResponse
import TestData
import com.deividasstr.revoratelut.di.KoinModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldEqual
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.KoinTest
import org.koin.test.get
import java.net.HttpURLConnection

@ExperimentalCoroutinesApi
class CurrencyRatesClientTest : KoinTest {

    private lateinit var server: MockWebServer

    @Before
    fun before() {
        server = MockWebServer()
        server.start()
    }

// Only for TDD testing the parsing of response
    @Test
    fun `when currency rates requested, should parse and return correct data`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(CurrencyRatesTestResponse.response))
        val baseUrl = server.url("/").toString()

        startKoin { modules(KoinModules.get(baseUrl)) }

        val service: CurrencyRatesClient = get()
        val response = runBlocking { service.getCurrentRates(TestData.eur) }

        val expectedResponse = TestData.currencyRatesResponse
        response shouldEqual expectedResponse
    }

    @After
    fun after() {
        server.shutdown()
    }
}