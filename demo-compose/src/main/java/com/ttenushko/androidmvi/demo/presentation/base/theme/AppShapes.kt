package com.ttenushko.androidmvi.demo.presentation.base.theme

import androidx.compose.material.Shapes
import androidx.compose.runtime.Stable

@Stable
class AppShapes(
    val materialShapes: Shapes
)

val DefaultAppShapes = AppShapes(
    materialShapes = Shapes()
)