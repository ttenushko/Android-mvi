package com.ttenushko.androidmvi.demo.common.domain.usecase

interface MultiResultUseCase<P : Any, R : Any> {

    fun execute(param: P, callback: Callback<R>): Cancellable

    interface Callback<R : Any> {
        fun onResult(result: R)
        fun onError(error: Throwable)
        fun onComplete()
    }
}