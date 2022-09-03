package com.ttenushko.androidmvi.demo.common.domain.usecase

interface SingleResultUseCase<P : Any, R : Any> {

    fun execute(param: P, callback: Callback<R>): Cancellable

    interface Callback<R : Any> {
        fun onComplete(result: R)
        fun onError(error: Throwable)
    }
}