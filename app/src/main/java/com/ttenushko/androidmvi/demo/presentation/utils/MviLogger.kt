package com.ttenushko.androidmvi.demo.presentation.utils

import android.util.Log
import com.ttenushko.androidmvi.LoggingMiddleware
import java.text.SimpleDateFormat
import java.util.*

class MviLogger<A : Any, S : Any>(private val tag: String) : LoggingMiddleware.Logger<A, S> {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    override fun log(action: A, oldState: S, newState: S) {
        val date = Date()
        Log.d(tag, "┌───→ Action: ${action.javaClass.simpleName} ${dateFormat.format(date)}")
        Log.d(tag, "├─ Prev state ► $oldState")
        Log.d(tag, "├─ Action     ► $action")
        Log.d(tag, "├─ Next state ► $newState")
        Log.d(tag, "└────────────────────────────────────────────────────────────────────")
    }
}