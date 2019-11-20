package com.deividasstr.revoratelut.data.network

import retrofit2.HttpException
import java.io.IOException

object NetworkCoroutineHelper {

    // Exception handler for coroutine calls.
    suspend fun <Response> safeApiCall(
        apiCall: suspend () -> Response
    ): NetworkResultWrapper<Response> {
        return try {
            NetworkResultWrapper.Success(apiCall())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> NetworkResultWrapper.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    NetworkResultWrapper.GenericError(code)
                }
                else -> {
                    NetworkResultWrapper.GenericError()
                }
            }
        }
    }
}