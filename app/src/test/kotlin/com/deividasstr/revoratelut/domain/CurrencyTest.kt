package com.deividasstr.revoratelut.domain

import org.junit.Test

class CurrencyTest {

    @Test(expected = IllegalArgumentException::class)
    fun `when currency code is shorter than required length, should receive IllegalArgumentException`() {
        Currency("US")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when currency code is longer than required length, should receive IllegalArgumentException`() {
        Currency("USDU")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when currency code contains digits, should receive IllegalArgumentException`() {
        Currency("1SD")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when currency code contains symbols, should receive IllegalArgumentException`() {
        Currency("US%")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `when currency code contains lowercase letters, should receive IllegalArgumentException`() {
        Currency("Usd")
    }

    @Test
    fun `when currency code contains 3 uppercase letters, should pass`() {
        Currency("USD")
    }
}