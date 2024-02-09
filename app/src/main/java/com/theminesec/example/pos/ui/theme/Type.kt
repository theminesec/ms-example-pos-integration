package com.theminesec.example.pos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.theminesec.example.pos.R

// Set of Material typography styles to start with
private val jetBrainMono = FontFamily(
    Font(R.font.jbm_medium)
)

private val defaultTypography = Typography()
val Type = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = jetBrainMono),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = jetBrainMono),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = jetBrainMono),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = jetBrainMono),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = jetBrainMono),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = jetBrainMono),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = jetBrainMono),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = jetBrainMono),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = jetBrainMono),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = jetBrainMono, fontSize = 12.sp, lineHeight = 16.sp),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = jetBrainMono, fontSize = 12.sp, lineHeight = 16.sp),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = jetBrainMono, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = jetBrainMono, fontSize = 12.sp),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = jetBrainMono, fontSize = 10.sp),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = jetBrainMono, fontSize = 10.sp, lineHeight = 12.sp),
)
