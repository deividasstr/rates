package com.deividasstr.revoratelut.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> periodicalFlowFrom(frequencyMillis: Long, block: suspend () -> T): Flow<T> {
    return flow {
        while (true) { // Ugly
            val result = block()
            emit(result)

            // If flow is not observed anymore, delay will cancel
            delay(frequencyMillis)
        }
    }
}