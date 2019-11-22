package com.deividasstr.revoratelut.data.repository

sealed class RemoteFailure {

    object NetworkFailure: RemoteFailure()
    object GenericFailure: RemoteFailure()
}