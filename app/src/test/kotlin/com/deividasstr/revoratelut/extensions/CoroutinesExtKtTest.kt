package com.deividasstr.revoratelut.extensions

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withTimeoutOrNull
import org.amshove.kluent.shouldEqualTo
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@ExperimentalCoroutinesApi
class CoroutinesExtTest {

    private val intSource = { Random(5).nextInt() }
    private val bufferTime = 100

    @Test
    fun `when observing between 1 - 2 seconds with period of 1s, should return 2 responses`() = runBlockingTest {
        val results = mutableListOf<Int>()
        val testPeriod = TimeUnit.SECONDS.toMillis(1)

        withTimeoutOrNull(TimeUnit.SECONDS.toMillis(1) + bufferTime) {
            periodicalFlowFrom(testPeriod) {
                intSource()
            }.toCollection(results)
        }

        results.size shouldEqualTo 2
    }

    @Test
    fun `when observing between 11-12 seconds with period of 1s, should return 12 responses`() = runBlockingTest {
        val results = mutableListOf<Int>()
        val testPeriod = TimeUnit.SECONDS.toMillis(1)

        withTimeoutOrNull(TimeUnit.SECONDS.toMillis(11) + bufferTime) {
            periodicalFlowFrom(testPeriod) {
                intSource()
            }.toCollection(results)
        }

        results.size shouldEqualTo 12
    }
}