package com.ttenushko.androidmvi.demo.presentation.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ttenushko.androidmvi.demo.presentation.base.theme.AppTheme

@Composable
fun SmallProgress(modifier: Modifier) {
    Card(
        modifier = modifier
            .size(32.dp),
        elevation = 10.dp,
        shape = AppTheme.shapes.materialShapes.small
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(16.dp, 16.dp),
                strokeWidth = 2.dp,
                color = AppTheme.colors.materialColors.secondary
            )
        }
    }
}