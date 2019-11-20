package com.deividasstr.revoratelut.data.network

sealed class NetworkResultWrapper<out T> {

    data class Success<out T>(val value: T) : NetworkResultWrapper<T>()
    data class GenericError(val code: Int? = null) : NetworkResultWrapper<Nothing>()
    object NetworkError : NetworkResultWrapper<Nothing>()
}