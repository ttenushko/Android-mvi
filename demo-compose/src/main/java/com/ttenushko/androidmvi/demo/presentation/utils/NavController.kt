package com.ttenushko.androidmvi.demo.presentation.utils

fun parseRoute(route: String, vararg args: String): String =
    StringBuilder(route).apply {
        args.forEachIndexed { index, arg ->
            if (0 == index) {
                append("?$arg={$arg}")
            } else {
                append("&$arg={$arg}")
            }
        }
    }.toString()

fun makeRoute(route: String, vararg args: Pair<String, String>): String =
    StringBuilder(route).apply {
        args.forEachIndexed { index, (arg, value) ->
            if (0 == index) {
                append("?$arg=$value")
            } else {
                append("&$arg=$value")
            }
        }
    }.toString()