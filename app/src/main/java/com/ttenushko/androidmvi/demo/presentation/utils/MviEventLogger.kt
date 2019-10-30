package com.ttenushko.androidmvi.demo.presentation.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class MviEventLogger<E : Any>(private val tag: String) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    fun log(event: E) {
        val date = Date()
        Log.d(tag, "┌───→ Event: ${event.javaClass.simpleName} ${dateFormat.format(date)}")
        Log.d(tag, "├─ Event      ► $event")
        Log.d(tag, "└────────────────────────────────────────────────────────────────────")
    }
}