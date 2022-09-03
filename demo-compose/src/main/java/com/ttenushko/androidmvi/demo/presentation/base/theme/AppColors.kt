package com.ttenushko.androidmvi.demo.presentation.base.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class AppColors(
    val materialColors: Colors
)

val AppLightColorPalette = AppColors(
    materialColors = lightColors(
        surface = Color(0xFFFFFFFF),
        background = Color(0xFFFFFFFF),
        primary = Color(0xFFFFFFFF),
        secondary = Color(0xFF1976D2),
        secondaryVariant = Color(0xFFCCCCCC),
        onPrimary = Color(0xFF323232),
        onSecondary = Color(0xFFFFFFFF)
    )
)

val AppDarkColorPalette = AppColors(
    materialColors = darkColors(
        surface = Color(0xFF323232),
        background = Color(0xFF323232),
        primary = Color(0xFF323232),
        secondary = Color(0xFF1976D2),
        secondaryVariant = Color(0xFF7F7F7F),
        onPrimary = Color(0xFFCFCFCF),
        onSecondary = Color(0xFFCFCFCF)
    )
)