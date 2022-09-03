package com.ttenushko.androidmvi.demo.presentation.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.ttenushko.androidmvi.demo.common.domain.utils.isInternetConnectivityError
import com.ttenushko.androidmvi.demo.presentation.dialogs.SimpleDialogFragment
import com.ttenushko.androidmvi.demo.presentation.utils.showDialog

object DefaultErrorHandler {
    const val DLG_ERROR = "dlgError"

    fun showError(
        activity: FragmentActivity,
        message: String?,
        error: Throwable,
        vararg args: Any
    ) {
        showError(
            activity.supportFragmentManager,
            activity,
            message,
            error,
            DLG_ERROR,
            args
        )
    }

    fun showError(
        fragment: Fragment,
        message: String?,
        error: Throwable,
        vararg args: Any
    ) {
        showError(
            fragment.childFragmentManager,
            fragment.context!!,
            message,
            error,
            DLG_ERROR,
            args
        )
    }

    fun showError(
        fragment: Fragment,
        message: String?,
        error: Throwable,
        tag: String,
        vararg args: Any
    ) {
        showError(
            fragment.childFragmentManager,
            fragment.context!!,
            message,
            error,
            tag,
            args
        )
    }

    @Suppress("UNUSED_PARAMETER")
    private fun showError(
        fragmentManager: FragmentManager,
        context: Context,
        message: String?,
        error: Throwable,
        tag: String,
        vararg args: Any
    ) {
        val dialog = when {
            error.isInternetConnectivityError() -> {
                SimpleDialogFragment.newInstance(
                    "Error",
                    message ?: "Internet connection unavailable.",
                    "OK"
                )
            }
            else -> {
                SimpleDialogFragment.newInstance(
                    "Error",
                    message ?: "Error occurred.",
                    "OK"
                )
            }
        }
        fragmentManager.showDialog(dialog, tag)
    }
}