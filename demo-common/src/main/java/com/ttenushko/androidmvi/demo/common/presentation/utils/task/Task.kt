package com.ttenushko.androidmvi.demo.common.presentation.utils.task

import com.ttenushko.androidmvi.demo.common.domain.usecase.Cancellable

interface Task<P : Any, R : Any> {
    fun execute(param: P, callback: Callback<R>): Cancellable

    interface Callback<R : Any> {
        fun onResult(result: R)
        fun onComplete()
        fun onError(error: Throwable)
    }
}