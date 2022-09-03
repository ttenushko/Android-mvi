package com.ttenushko.androidmvi.demo.common.presentation.utils

import android.util.Log
import com.ttenushko.mvi.extra.MviLoggingMiddleware
import java.text.SimpleDateFormat
import java.util.*

class MviLogger<A : Any, S : Any>(private val tag: String) : MviLoggingMiddleware.Logger<A, S> {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    override fun log(action: A, oldState: S, newState: S) {
        val date = Date()
        if (newState != oldState) {
            Log.d(tag, "┌───→ Action: ${action.javaClass.simpleName} ${dateFormat.format(date)}")
            Log.d(tag, "├─ Action       ► $action")
            Log.d(tag, "├─ Prev state   ► $oldState")
            Log.d(tag, "├─ Next state   ► $newState")
            Log.d(tag, "└────────────────────────────────────────────────────────────────────")
        } else {
            Log.d(tag, "┌───→ Action: ${action.javaClass.simpleName} ${dateFormat.format(date)}")
            Log.d(tag, "├─ Action       ► $action")
            Log.d(tag, "├─ State (same) ► $oldState")
            Log.d(tag, "└────────────────────────────────────────────────────────────────────")
        }
    }
}