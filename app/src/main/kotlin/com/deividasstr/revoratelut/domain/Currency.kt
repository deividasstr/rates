package com.deividasstr.revoratelut.domain

data class Currency(val currencyCode: String) {

    init {
        require(currencyCode.length == currencyCodeLength) { "Currency code must be 3 letters long" }
        require(currencyCode.all { it.isLetter() && it.isUpperCase() })
        { "Currency code must contain only uppercase letters" }
    }

    companion object {
        private const val currencyCodeLength = 3
    }
}