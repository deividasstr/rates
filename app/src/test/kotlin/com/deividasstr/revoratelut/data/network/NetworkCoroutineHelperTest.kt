package com.deividasstr.revoratelut.data.network

import com.deividasstr.revoratelut.data.network.NetworkCoroutineHelper.safeApiCall
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.amshove.kluent.shouldEqual
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class NetworkCoroutineHelperTest {

    @Test
    fun `when lambda returns successfully then it should emit the result as success`() =
        runBlockingTest {
            val lambdaResult = true
            val result = NetworkCoroutineHelper.safeApiCall { lambdaResult }
            result shouldEqual NetworkResultWrapper.Success(lambdaResult)
        }

    @Test
    fun `when lambda throws IOException then it should emit the result as NetworkError`() =
        runBlockingTest {
            val result = safeApiCall { throw IOException() }
            result shouldEqual NetworkResultWrapper.NetworkError
        }

    @Test
    fun `when lambda throws HttpException then it should emit the result as GenericError`() =
        runBlockingTest {
            val errorBody = "{\"errors\": [\"Unexpected parameter\"]}".toResponseBody(null)
            val result = safeApiCall {
                throw HttpException(Response.error<Any>(422, errorBody))
            }

            result shouldEqual NetworkResultWrapper.GenericError(422)
        }

    @Test
    fun `when lambda throws unknown exception then it should emit GenericError`() =
        runBlockingTest {
            val result = safeApiCall {
                throw IllegalStateException()
            }
            result shouldEqual NetworkResultWrapper.GenericError()
        }
}