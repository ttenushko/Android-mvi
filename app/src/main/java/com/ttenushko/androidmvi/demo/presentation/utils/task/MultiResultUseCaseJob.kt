package com.ttenushko.androidmvi.demo.presentation.utils.task

import com.ttenushko.androidmvi.demo.domain.usecase.MultiResultUseCase
import com.ttenushko.androidmvi.demo.domain.utils.Cancellable
import java.util.*


class MultiResultUseCaseJob<R : Any, T>(
    creator: (MultiResultUseCase.Callback<R>) -> Cancellable,
    private val tag: T
) : MultiResultJob<R, T> {

    companion object {
        private const val STATE_RUNNING = 1
        private const val STATE_SUCCEED = 2
        private const val STATE_FAIL = 3
    }

    @Volatile
    private var state = STATE_RUNNING
    @Volatile
    private var jobResult: R? = null
    @Volatile
    private lateinit var jobError: Throwable
    private val events = ValueQueueDrain<Event<R, T>> { event -> handleEvent(event) }
    private val callbacks = LinkedHashSet<MultiResultJob.Callback<R, T>>()
    private val useCaseCancellable: Cancellable
    private val useCaseCallback = object : MultiResultUseCase.Callback<R> {
        override fun onResult(result: R) {
            events.drain(Event.result(result))
        }

        override fun onComplete() {
            events.drain(Event.succeed())
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

    override fun addCallback(callback: MultiResultJob.Callback<R, T>) {
        events.drain(Event.addCallback(callback))
    }

    override fun removeCallback(callback: MultiResultJob.Callback<R, T>) {
        events.drain(Event.removeCallback(callback))
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
            Event.TYPE_RESULT -> {
                handleResult(event.result as R)
            }
            Event.TYPE_SUCCEED -> {
                handleSucceed()
            }
            Event.TYPE_FAIL -> {
                handleFail(event.error!!)
            }
            else -> {
                throw IllegalArgumentException("Unsupported event type: ${event.type}")
            }
        }
    }

    private fun handleAddCallback(callback: MultiResultJob.Callback<R, T>) {
        if (callbacks.add(callback)) {
            when (state) {
                STATE_RUNNING -> {
                    jobResult?.let { callback.onResult(this@MultiResultUseCaseJob, it, tag) }
                }
                STATE_SUCCEED -> {
                    callback.onComplete(this@MultiResultUseCaseJob, tag)
                }
                STATE_FAIL -> {
                    callback.onFailure(this@MultiResultUseCaseJob, jobError, tag)
                }
            }
        }
    }

    private fun handleRemoveCallback(callback: MultiResultJob.Callback<R, T>) {
        callbacks.remove(callback)
    }

    private fun handleResult(result: R) {
        check(STATE_RUNNING == state)
        jobResult = result
        callbacks.forEach { it.onResult(this@MultiResultUseCaseJob, result, tag) }
    }

    private fun handleSucceed() {
        check(STATE_RUNNING == state)
        jobResult = null
        state = STATE_SUCCEED
        callbacks.forEach { it.onComplete(this@MultiResultUseCaseJob, tag) }
    }

    private fun handleFail(error: Throwable) {
        check(STATE_RUNNING == state)
        jobResult = null
        jobError = error
        state = STATE_FAIL
        callbacks.forEach { it.onFailure(this@MultiResultUseCaseJob, error, tag) }
    }

    // TODO: add object pool
    private data class Event<R : Any, T>(
        val type: Int,
        val callback: MultiResultJob.Callback<R, T>? = null,
        val result: R? = null,
        val error: Throwable? = null
    ) {
        companion object {
            const val TYPE_ADD_CALLBACK = 1
            const val TYPE_REMOVE_CALLBACK = 2
            const val TYPE_RESULT = 3
            const val TYPE_SUCCEED = 4
            const val TYPE_FAIL = 5

            fun <R : Any, T> addCallback(callback: MultiResultJob.Callback<R, T>): Event<R, T> =
                Event(
                    type = TYPE_ADD_CALLBACK,
                    callback = callback
                )

            fun <R : Any, T> removeCallback(callback: MultiResultJob.Callback<R, T>): Event<R, T> =
                Event(
                    type = TYPE_REMOVE_CALLBACK,
                    callback = callback
                )

            fun <R : Any, T> result(result: R): Event<R, T> =
                Event(
                    type = TYPE_RESULT,
                    result = result
                )

            fun <R : Any, T> succeed(): Event<R, T> =
                Event(
                    type = TYPE_SUCCEED
                )

            fun <R : Any, T> fail(error: Throwable): Event<R, T> =
                Event(
                    type = TYPE_FAIL,
                    error = error
                )
        }
    }
}

fun <P : Any, R : Any, T> MultiResultUseCase<P, R>.asMultiResultJob(
    param: P,
    tag: T
): MultiResultJob<R, T> =
    MultiResultUseCaseJob(
        { callback -> this.execute(param, callback) },
        tag
    )
