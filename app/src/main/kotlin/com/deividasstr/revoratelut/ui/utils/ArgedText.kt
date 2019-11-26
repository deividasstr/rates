package com.deividasstr.revoratelut.ui.utils

import android.content.res.Resources
import androidx.annotation.StringRes
import com.deividasstr.revoratelut.ui.utils.ArgedText.Companion.NO_RESOURCE_STRING

/**
 * Class wrapping {@link android.support.annotation.StringRes} and possible varargs for formatting a string.
 *
 * @param textRes resource id of String
 * @param args unlimited String varargs
 *
 * Returns the formatted string with {@link #getString}
 */
class ArgedText(
        @StringRes private val textRes: Int,
        private vararg val args: String
) {

    init {
        require(textRes != NO_RESOURCE_STRING || args.isNotEmpty()) {
            "Text resource OR string must be present" }
    }

    /**
     * Formats the parameters of the class and returns formatted string
     *
     * @param resources Android resources to find string in.
     *
     * @return formatted strings
     */
    fun getString(resources: Resources): String = when (textRes) {
        NO_RESOURCE_STRING -> args.first()
        else               -> textRes.let { resources.getString(it, *args) }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ArgedText) return false
        return textRes == other.textRes && args.contentEquals(other.args)
    }

    override fun hashCode(): Int {
        var result = textRes
        result = 31 * result + args.contentHashCode()
        return result
    }

    companion object {
        const val NO_RESOURCE_STRING = -1
    }
}

fun Int.toArgedText(): ArgedText = ArgedText(this)
fun String.toArgedText(): ArgedText = ArgedText(NO_RESOURCE_STRING, this)