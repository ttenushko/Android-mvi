package com.ttenushko.androidmvi.demo.presentation.utils.task

import com.ttenushko.androidmvi.demo.domain.utils.Cancellable

interface SingleResultJob<R : Any, T> : Cancellable {
    val isComplete: Boolean
    val isSucceed: Boolean
    val result: R
    val error: Throwable

    fun addCallback(callback: Callback<R, T>)
    fun removeCallback(callback: Callback<R, T>)

    interface Callback<R : Any, T> {
        fun onResult(job: SingleResultJob<R, T>, result: R, tag: T)
        fun onFailure(job: SingleResultJob<R, T>, error: Throwable, tag: T)
    }
}
