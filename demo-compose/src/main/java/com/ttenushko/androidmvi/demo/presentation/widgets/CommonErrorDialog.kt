package com.ttenushko.androidmvi.demo.presentation.widgets

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.common.domain.utils.isInternetConnectivityError

@Composable
fun CommonErrorDialog(error: Throwable, onDismiss: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.error_title))
        },
        text = {
            Text(
                text = stringResource(
                    id = when {
                        error.isInternetConnectivityError() -> R.string.error_internet_connection_message
                        else -> R.string.error_generic_message
                    }
                )
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = android.R.string.ok))
            }
        }
    )
}