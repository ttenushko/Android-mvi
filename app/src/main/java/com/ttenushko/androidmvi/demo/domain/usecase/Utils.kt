package com.ttenushko.androidmvi.demo.domain.usecase

import com.ttenushko.androidmvi.demo.domain.utils.Cancellable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlin.coroutines.resume

internal suspend fun CoroutineScope.awaitCancellation(block: () -> Unit = {}) {
    try {
        suspendCancellableCoroutine<Unit> { cont ->
            cont.invokeOnCancellation { cont.resume(Unit) }
        }
    } finally {
        block()
    }
}

internal fun CoroutineScope.asCancellable(): Cancellable =
    object : Cancellable {
        override val isCancelled: Boolean
            get() = (coroutineContext[Job] as Job).isCancelled

        override fun cancel() {
            (coroutineContext[Job] as Job).cancel()
        }
    }