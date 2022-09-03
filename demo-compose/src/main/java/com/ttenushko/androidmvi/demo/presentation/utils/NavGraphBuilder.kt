package com.ttenushko.androidmvi.demo.presentation.utils

import android.os.Bundle
import androidx.navigation.NavBackStackEntry

fun NavBackStackEntry.requireArguments(): Bundle =
    requireNotNull(arguments) { "Arguments not set." }