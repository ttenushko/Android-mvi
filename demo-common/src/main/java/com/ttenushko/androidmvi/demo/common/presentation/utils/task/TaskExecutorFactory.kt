package com.ttenushko.androidmvi.demo.common.presentation.utils.task

import kotlinx.coroutines.CoroutineScope

interface TaskExecutorFactory {
    fun <P : Any, R : Any, T> createTaskExecutor(task: Task<P, R>): TaskExecutor<P, R, T>

    companion object {
        fun create(coroutineScope: CoroutineScope): TaskExecutorFactory =
            object : TaskExecutorFactory {
                override fun <P : Any, R : Any, T> createTaskExecutor(task: Task<P, R>): TaskExecutor<P, R, T> =
                    CoroutineTaskExecutor(coroutineScope, task)
            }
    }
}

fun <P : Any, R : Any, T> TaskExecutorFactory.createTaskExecutor(
    task: Task<P, R>,
    startHandler: ((T) -> Unit)? = null,
    resultHandler: ((R, T) -> Unit)? = null,
    completeHandler: ((T) -> Unit)? = null,
    errorHandler: ((error: Throwable, T) -> Unit)? = null
): TaskExecutor<P, R, T> =
    this@createTaskExecutor.createTaskExecutor<P, R, T>(task).apply {
        this.startHandler = startHandler
        this.resultHandler = resultHandler
        this.completeHandler = completeHandler
        this.errorHandler = errorHandler
    }
