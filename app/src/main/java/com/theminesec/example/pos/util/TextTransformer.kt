package com.theminesec.example.pos.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText

fun transformActivationCode(raw: AnnotatedString): TransformedText {
    val formatted = raw.chunked(4).joinToString("-")

    /**
     * conversion from raw str -> transformed str mapping cursor position
     * so 4th char of raw become the 5th char in the transformed str
     * so forth and so on
     * same for backward
     */
    val activationCodeOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int =
            when (offset) {
                in (1..4) -> offset
                in (5..8) -> offset + 1
                in (9..12) -> offset + 2
                in (13..16) -> offset + 3
                else -> offset
            }

        // 1234-6789-1234-6789
        override fun transformedToOriginal(offset: Int): Int =
            when (offset) {
                in (1..4) -> offset
                in (5..9) -> offset - 1
                in (11..14) -> offset - 2
                in (15..19) -> offset - 3
                else -> offset
            }
    }
    return TransformedText(AnnotatedString(formatted), activationCodeOffsetTranslator)
}
