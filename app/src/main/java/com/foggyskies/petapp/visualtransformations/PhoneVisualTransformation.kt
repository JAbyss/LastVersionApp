package com.foggyskies.testingscrollcompose.visualtransformations

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation: VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        var result = ""

        text.forEachIndexed { index, c ->
            when (index) {
                0 -> result = "+$c "
                1 -> result += "($c"
                2 -> result += c
                3 -> result += "$c) "
                4, 5, 6 -> result += c
                7 -> result += "-$c"
                8 -> result += c
                9 -> result += "-$c"
                10 -> result += c
            }
        }

        val offsetPhone = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when (offset) {
                    1 -> {
                        offset + 1
                    }
                    2,3 -> {
                        offset + 3
                    }
                    4 -> offset + 4
                    5,6,7 -> offset + 5
                    8,9 -> offset + 6
                    10,11 -> offset + 7
                    else -> 0
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when (offset) {
                    1 -> {
                        offset - 1
                    }
                    2, 3 -> {
                        offset - 3
                    }
                    4 -> offset - 4
                    5,6,7 -> offset - 5
                    8,9 -> offset - 6
                    10,11 -> offset - 7
                    else -> 0
                }
            }
        }

        return TransformedText(AnnotatedString(result), offsetPhone)
    }
}