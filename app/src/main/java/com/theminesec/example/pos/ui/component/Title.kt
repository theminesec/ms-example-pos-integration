package com.theminesec.example.pos.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun Title(text: String) = Text(text, style = MaterialTheme.typography.titleMedium)
