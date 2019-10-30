package com.ttenushko.androidmvi.demo.presentation.utils.task

import com.ttenushko.androidmvi.demo.domain.usecase.SingleResultUseCase
import com.ttenushko.androidmvi.demo.domain.utils.Cancellable

class SingleResultUseCaseJob<R : Any, T>(
    creator: (SingleResultUseCase.Callback<R>) -> Cancellable,
    private val tag: T
) : SingleResultJob<R, T> {

    companion object {
        private const val STATE_RUNNING = 1
        private const val STATE_SUCCEED = 2
        private const val STATE_FAIL = 3
    }

    @Volatile
    private var state = STATE_RUNNING
    @Volatile
    private lateinit var jobResult: R
    @Volatile
    private lateinit var jobError: Throwable
    private val events = ValueQueueDrain<Event<R, T>> { event -> handleEvent(event) }
    private val callbacks = LinkedHashSet<SingleResultJob.Callback<R, T>>()
    private val useCaseCancellable: Cancellable
    private val useCaseCallback = object : SingleResultUseCase.Callback<R> {
        override fun onComplete(result: R) {
            events.drain(Event.succeed(result))
        }

        override fun onError(error: Throwable) {
            events.drain(Event.fail(error))
        }
    }
    override val isCancelled: Boolean
        get() = useCaseCancellable.isCancelled
    override val isComplete: Boolean
        get() = state.let { (STATE_SUCCEED == it || STATE_FAIL == it) }
    override val isSucceed: Boolean
        get() = (STATE_SUCCEED == state)
    override val result: R
        get() = when (val state = this.state) {
            STATE_SUCCEED -> jobResult
            STATE_RUNNING -> throw IllegalStateException("Job is running")
            STATE_FAIL -> throw IllegalStateException("Job failed")
            else -> throw IllegalStateException("Unknown job state: $state")
        }
    override val error: Throwable
        get() = when (val state = this.state) {
            STATE_FAIL -> jobError
            STATE_RUNNING -> throw IllegalStateException("Job is running")
            STATE_SUCCEED -> throw IllegalStateException("Job is complete")
            else -> throw IllegalStateException("Unknown job state: $state")
        }

    init {
        useCaseCancellable = creator(useCaseCallback)
    }

    override fun addCallback(callback: SingleResultJob.Callback<R, T>) {
        events.drain(Event.addCallback(callback))
    }

    override fun removeCallback(callback: SingleResultJob.Callback<R, T>) {
        callbacks.remove(callback)
    }

    override fun cancel() {
        useCaseCancellable.cancel()
    }

    private fun handleEvent(event: Event<R, T>) {
        when (event.type) {
            Event.TYPE_ADD_CALLBACK -> {
                handleAddCallback(event.callback!!)
            }
            Event.TYPE_REMOVE_CALLBACK -> {
                handleRemoveCallback(event.callback!!)
            }
            Event.TYPE_SUCCEED -> {
                handleSucceed(event.result!!)
            }
            Event.TYPE_FAIL -> {
                handleFail(event.error!!)
            }
            else -> {
                throw IllegalArgumentException("Unsupported event type: ${event.type}")
            }
        }
    }

    private fun handleAddCallback(callback: SingleResultJob.Callback<R, T>) {
        if (callbacks.add(callback)) {
            when (state) {
                STATE_RUNNING -> {
                    // do nothing
                }
                STATE_SUCCEED -> {
                    callback.onResult(this@SingleResultUseCaseJob, jobResult, tag)
                }
                STATE_FAIL -> {
                    callback.onFailure(this@SingleResultUseCaseJob, jobError, tag)
                }
            }
        }
    }

    private fun handleRemoveCallback(callback: SingleResultJob.Callback<R, T>) {
        callbacks.remove(callback)
    }

    private fun handleSucceed(result: R) {
        check(STATE_RUNNING == state)
        jobResult = result
        state = STATE_SUCCEED
        callbacks.forEach { it.onResult(this@SingleResultUseCaseJob, result, tag) }
    }

    private fun handleFail(error: Throwable) {
        check(STATE_RUNNING == state)
        jobError = error
        state = STATE_FAIL
        callbacks.forEach { it.onFailure(this@SingleResultUseCaseJob, error, tag) }
    }

    // TODO: add object pool
    private data class Event<R : Any, T>(
        val type: Int,
        val callback: SingleResultJob.Callback<R, T>? = null,
        val result: R? = null,
        val error: Throwable? = null
    ) {
        companion object {
            const val TYPE_ADD_CALLBACK = 1
            const val TYPE_REMOVE_CALLBACK = 2
            const val TYPE_SUCCEED = 3
            const val TYPE_FAIL = 4

            fun <R : Any, T> addCallback(callback: SingleResultJob.Callback<R, T>): Event<R, T> =
                Event(
                    type = TYPE_ADD_CALLBACK,
                    callback = callback
                )

            fun <R : Any, T> removeCallback(callback: SingleResultJob.Callback<R, T>): Event<R, T> =
                Event(
                    type = TYPE_REMOVE_CALLBACK,
                    callback = callback
                )

            fun <R : Any, T> succeed(result: R): Event<R, T> =
                Event(
                    type = TYPE_SUCCEED,
                    result = result
                )

            fun <R : Any, T> fail(error: Throwable): Event<R, T> =
                Event(
                    type = TYPE_FAIL,
                    error = error
                )
        }
    }
}

fun <P : Any, R : Any, T> SingleResultUseCase<P, R>.asSingleResultJob(
    param: P,
    tag: T
): SingleResultJob<R, T> =
    SingleResultUseCaseJob(
        { callback -> this.execute(param, callback) },
        tag
    )
