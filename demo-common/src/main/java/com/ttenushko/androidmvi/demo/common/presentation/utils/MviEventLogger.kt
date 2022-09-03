package com.ttenushko.androidmvi.demo.common.presentation.utils

import android.util.Log
import com.ttenushko.mvi.MviStore
import java.text.SimpleDateFormat
import java.util.*

class MviEventLogger<E : Any>(private val tag: String) : MviStore.EventListener<E> {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    override fun onEvent(event: E) {
        val date = Date()
        Log.d(tag, "┌───→ Event:  ${event.javaClass.simpleName} ${dateFormat.format(date)}")
        Log.d(tag, "├─ Event        ► $event")
        Log.d(tag, "└────────────────────────────────────────────────────────────────────")
    }
}