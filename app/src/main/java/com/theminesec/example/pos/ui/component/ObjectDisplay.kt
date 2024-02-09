package com.theminesec.example.pos.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ObjectDisplay(text: String) {
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm:ss.SSS") }

    Row {
        Text(
            style = MaterialTheme.typography.labelSmall, color = Color(0xff28fe14),
            text = formatter.format(LocalDateTime.now()),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            style = MaterialTheme.typography.labelSmall, color = Color(0xff28fe14),
            text = text
        )
    }
}
