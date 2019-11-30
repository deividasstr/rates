package com.deividasstr.revoratelut.domain

import java.math.BigDecimal
import java.math.MathContext

class Calculator {

    fun multiply(first: BigDecimal, second: BigDecimal): BigDecimal {
        return first.multiply(second, MathContext.DECIMAL64)
    }
}