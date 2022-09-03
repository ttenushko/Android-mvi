package com.ttenushko.androidmvi.demo.presentation.utils

import android.view.View

var View.isVisible: Boolean
    get() = View.VISIBLE == visibility
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }