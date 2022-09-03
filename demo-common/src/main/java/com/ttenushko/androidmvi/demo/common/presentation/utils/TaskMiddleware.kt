package com.ttenushko.androidmvi.demo.common.presentation.utils

import com.ttenushko.androidmvi.demo.common.presentation.utils.task.Task
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutor
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.TaskExecutorFactory
import com.ttenushko.androidmvi.demo.common.presentation.utils.task.createTaskExecutor
import com.ttenushko.mvi.Dispatcher
import com.ttenushko.mvi.MviMiddleware
import com.ttenushko.mvi.MviMiddlewareImpl

class TaskMiddleware<A, S, E, P : Any, R : Any, T>(
    task: Task<P, R>,
    taskExecutorFactory: TaskExecutorFactory,
    startHandler: (MviMiddlewareScope<A, S, E>.(T) -> Unit)? = null,
    resultHandler: (MviMiddlewareScope<A, S, E>.(R, T) -> Unit)? = null,
    completeHandler: (MviMiddlewareScope<A, S, E>.(T) -> Unit)? = null,
    errorHandler: (MviMiddlewareScope<A, S, E>.(error: Throwable, T) -> Unit)? = null,
    private val applyHandler: MviMiddlewareScope<A, S, E>.(action: A, chain: MviMiddleware.Chain, task: TaskExecutor<P, R, T>) -> Unit
) : MviMiddlewareImpl<A, S, E>() {

    private val mviMiddlewareScope = object : MviMiddlewareScope<A, S, E> {
        override val state: S
            get() {
                return this@TaskMiddleware.state
            }
        override val dispatchAction: Dispatcher<A>
            get() {
                return this@TaskMiddleware.dispatchAction
            }
        override val dispatchEvent: Dispatcher<E>
            get() {
                return this@TaskMiddleware.dispatchEvent
            }
    }
    private val task: TaskExecutor<P, R, T> =
        taskExecutorFactory.createTaskExecutor(
            task = task,
            startHandler = startHandler?.let { handler ->
                { tag ->
                    handler(mviMiddlewareScope, tag)
                }
            },
            resultHandler = resultHandler?.let { handler ->
                { result, tag ->
                    handler(mviMiddlewareScope, result, tag)
                }
            },
            completeHandler = completeHandler?.let { handler ->
                { tag ->
                    handler(mviMiddlewareScope, tag)
                }
            },
            errorHandler = errorHandler?.let { handler ->
                { error, tag ->
                    handler(mviMiddlewareScope, error, tag)
                }
            }
        )

    override fun onApply(action: A, chain: MviMiddleware.Chain) {
        applyHandler(mviMiddlewareScope, action, chain, task)
    }

    override fun onClose() {
        task.stop()
    }

    interface MviMiddlewareScope<A, S, E> {
        val state: S
        val dispatchAction: Dispatcher<A>
        val dispatchEvent: Dispatcher<E>
    }
}