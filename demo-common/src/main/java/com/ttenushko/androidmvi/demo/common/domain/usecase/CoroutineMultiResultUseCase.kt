package com.ttenushko.androidmvi.demo.common.domain.usecase

import kotlinx.coroutines.*

abstract class CoroutineMultiResultUseCase<P : Any, R : Any>(
    private val dispatcher: CoroutineDispatcher
) : MultiResultUseCase<P, R> {

    final override fun execute(param: P, callback: MultiResultUseCase.Callback<R>): Cancellable =
        CoroutineScope(dispatcher + Job()).let { coroutineScope ->
            coroutineScope.launch {
                try {
                    run(param, { result: R -> callback.onResult(result) })
                    callback.onComplete()
                } catch (error: Throwable) {
                    callback.onError(error)
                }
            }.also { job ->
                job.invokeOnCompletion { coroutineScope.cancel() }
            }
            coroutineScope.asCancellable()
        }

    protected abstract suspend fun run(param: P, resultSender: (R) -> Unit)
}