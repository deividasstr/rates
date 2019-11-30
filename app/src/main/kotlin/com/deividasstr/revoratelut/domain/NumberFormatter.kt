package com.deividasstr.revoratelut.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

class NumberFormatter {

    // Format subclasses are not thread safe
    private val formatter = object : ThreadLocal<NumberFormat>() {
        override fun initialValue(): NumberFormat {
            return NumberFormat.getNumberInstance().apply {
                roundingMode = RoundingMode.HALF_DOWN
                maximumIntegerDigits = 7
                maximumFractionDigits = 2
            }
        }
    }

    fun format(number: BigDecimal): String = formatter.get()!!.format(number)

    fun parseOrZero(number: String): BigDecimal {
        val checkedNumber = if (isInvalidNumberString(number)) "0.00" else number
        return formatter.get()!!.parse(checkedNumber)!!.toDouble().toBigDecimal()
    }

    private fun isInvalidNumberString(newBaseRate: String) =
        newBaseRate.isBlank() || newBaseRate == "." || newBaseRate == ","
}