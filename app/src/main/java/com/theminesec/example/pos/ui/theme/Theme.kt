package com.theminesec.example.pos.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

val ShamrockGreen = Color(0xff2BD659)
val SlateGray = Color(0xff64748b)
val CatalinaBlue = Color(0xff101A2D)

@Composable
fun MsExampleTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = ShamrockGreen,
        onPrimary = CatalinaBlue,
        secondary = SlateGray,
        background = CatalinaBlue,
        onBackground = Color.White
    )
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Type,
        content = content
    )
}
