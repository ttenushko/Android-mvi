package com.ttenushko.androidmvi.demo.presentation.base.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Stable

@Stable
class AppTypography(
    val materialTypography: Typography
)

val DefaultAppTypography = AppTypography(
    materialTypography = Typography()
)
