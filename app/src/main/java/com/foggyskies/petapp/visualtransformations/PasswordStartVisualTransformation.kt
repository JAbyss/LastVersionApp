package com.foggyskies.testingscrollcompose.visualtransformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PasswordStartVisualTransformation: VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {

        var result = ""

        text.forEachIndexed { _, _ ->
            result += "* "
        }

        val offsetPassword = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + offset
//                else 0
            }

            override fun transformedToOriginal(offset: Int): Int {
                return offset - offset
            }
        }

        return TransformedText(AnnotatedString(result), offsetPassword)
    }
}