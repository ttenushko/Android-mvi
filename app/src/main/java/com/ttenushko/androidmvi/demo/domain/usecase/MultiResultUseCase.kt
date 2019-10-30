package com.ttenushko.androidmvi.demo.domain.usecase

import com.ttenushko.androidmvi.demo.domain.utils.Cancellable

interface MultiResultUseCase<P : Any, R : Any> {

    fun execute(param: P, callback: Callback<R>): Cancellable

    interface Callback<R : Any> {
        fun onResult(result: R)
        fun onError(error: Throwable)
        fun onComplete()
    }
}