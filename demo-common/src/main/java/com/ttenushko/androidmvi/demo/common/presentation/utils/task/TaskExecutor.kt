package com.ttenushko.androidmvi.demo.common.presentation.utils.task

import kotlinx.coroutines.CoroutineScope


interface TaskExecutor<P : Any, R : Any, T> {
    var startHandler: ((T) -> Unit)?
    var resultHandler: ((R, T) -> Unit)?
    var completeHandler: ((T) -> Unit)?
    var errorHandler: ((Throwable, T) -> Unit)?
    val isRunning: Boolean

    fun start(param: P, tag: T): Boolean
    fun stop(): Boolean

    companion object {
        fun <P : Any, R : Any, T> create(
            coroutineScope: CoroutineScope,
            task: Task<P, R>
        ): TaskExecutor<P, R, T> =
            CoroutineTaskExecutor(coroutineScope, task)
    }
}

fun <P : Any, R : Any, T> createTaskExecutor(
    coroutineScope: CoroutineScope,
    task: Task<P, R>,
    startHandler: ((T) -> Unit)? = null,
    resultHandler: ((R, T) -> Unit)? = null,
    completeHandler: ((T) -> Unit)? = null,
    errorHandler: ((error: Throwable, T) -> Unit)? = null
): TaskExecutor<P, R, T> =
    CoroutineTaskExecutor<P, R, T>(coroutineScope, task)
        .apply {
            this.startHandler = startHandler
            this.resultHandler = resultHandler
            this.completeHandler = completeHandler
            this.errorHandler = errorHandler
        }