package com.ttenushko.androidmvi.demo.common.domain.usecase

import kotlinx.coroutines.*

abstract class CoroutineSingleResultUseCase<P : Any, R : Any>(
    private val dispatcher: CoroutineDispatcher
) : SingleResultUseCase<P, R> {

    final override fun execute(param: P, callback: SingleResultUseCase.Callback<R>): Cancellable =
        CoroutineScope(dispatcher + Job()).let { coroutineScope ->
            coroutineScope.launch {
                try {
                    val result = run(param)
                    callback.onComplete(result)
                } catch (error: Throwable) {
                    callback.onError(error)
                }
            }.also { job ->
                job.invokeOnCompletion { coroutineScope.cancel() }
            }
            coroutineScope.asCancellable()
        }

    protected abstract suspend fun run(param: P): R
}