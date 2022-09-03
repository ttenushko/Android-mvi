package com.ttenushko.androidmvi.demo.presentation.base.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController

val LocalAppColors =
    staticCompositionLocalOf<AppColors> { throw IllegalStateException("AppColors not set.") }
val LocalAppTypography =
    staticCompositionLocalOf<AppTypography> { throw IllegalStateException("AppTypography not set.") }
val LocalAppShapes =
    staticCompositionLocalOf<AppShapes> { throw IllegalStateException("AppShapes not set.") }

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current

    val shapes: AppShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val colors = if (darkTheme) AppDarkColorPalette else AppLightColorPalette
    val typography = DefaultAppTypography
    val shapes = DefaultAppShapes

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = colors.materialColors.surface,
            darkIcons = !darkTheme
        )
    }

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides typography,
        LocalAppShapes provides shapes
    ) {
        MaterialTheme(
            colors = colors.materialColors,
            typography = typography.materialTypography,
            shapes = shapes.materialShapes
        ) {
            content()
        }
    }
}