package com.ttenushko.androidmvi.demo.domain.usecase

import com.ttenushko.androidmvi.demo.domain.utils.Cancellable
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class CoroutineSingleResultUseCase<P : Any, R : Any>(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SingleResultUseCase<P, R> {

    companion object {
        private fun createCoroutineScope(defaultDispatcher: CoroutineDispatcher): CoroutineScope =
            object : CoroutineScope {
                override val coroutineContext: CoroutineContext by lazy { SupervisorJob() + defaultDispatcher }
            }
    }

    @Suppress("DeferredResultUnused")
    final override fun execute(
        param: P,
        callback: SingleResultUseCase.Callback<R>
    ): Cancellable =
        createCoroutineScope(defaultDispatcher).let { coroutineScope ->
            coroutineScope.launch {
                val result = try {
                    run(param)
                } catch (error: Throwable) {
                    callback.onError(error)
                    null
                }
                if (null != result) {
                    callback.onComplete(result)
                }
            }
            coroutineScope.asCancellable()
        }

    protected abstract suspend fun run(param: P): R
}
