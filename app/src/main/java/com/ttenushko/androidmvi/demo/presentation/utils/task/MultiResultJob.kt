package com.ttenushko.androidmvi.demo.presentation.utils.task

import com.ttenushko.androidmvi.demo.domain.utils.Cancellable

interface MultiResultJob<R : Any, T> : Cancellable {
    val isComplete: Boolean
    val isSucceed: Boolean
    val error: Throwable

    fun addCallback(callback: Callback<R, T>)
    fun removeCallback(callback: Callback<R, T>)

    interface Callback<R : Any, T> {
        fun onResult(job: MultiResultJob<R, T>, result: R, tag: T)
        fun onComplete(job: MultiResultJob<R, T>, tag: T)
        fun onFailure(job: MultiResultJob<R, T>, error: Throwable, tag: T)
    }
}
