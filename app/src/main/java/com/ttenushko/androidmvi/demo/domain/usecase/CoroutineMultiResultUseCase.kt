package com.ttenushko.androidmvi.demo.domain.usecase

import com.ttenushko.androidmvi.demo.domain.utils.Cancellable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

abstract class CoroutineMultiResultUseCase<P : Any, R : Any>(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val channelCapacity: Int = Channel.UNLIMITED
) : MultiResultUseCase<P, R> {

    companion object {
        private fun createCoroutineScope(defaultDispatcher: CoroutineDispatcher): CoroutineScope =
            object : CoroutineScope {
                override val coroutineContext: CoroutineContext by lazy { SupervisorJob() + defaultDispatcher }
            }
    }

    @Suppress("DeferredResultUnused")
    final override fun execute(param: P, callback: MultiResultUseCase.Callback<R>): Cancellable =
        createCoroutineScope(defaultDispatcher).let { coroutineScope ->
            val actor = coroutineScope.actor<R>(capacity = channelCapacity) {
                consumeEach { result ->
                    callback.onResult(result)
                }
            }
            coroutineScope.async {
                try {
                    run(param, actor)
                    callback.onComplete()
                } catch (error: Throwable) {
                    callback.onError(error)
                }
            }
            coroutineScope.asCancellable()
        }

    protected abstract suspend fun run(param: P, channel: SendChannel<R>)
}
