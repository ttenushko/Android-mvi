package com.ttenushko.androidmvi.demo.presentation.utils.task

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

class SingleResultTask<P : Any, R : Any, T>(
    val name: String,
    private val dispatcher: CoroutineDispatcher,
    private val creator: (P, T) -> SingleResultJob<R, T>,
    private val resultHandler: ((R, T) -> Unit)?,
    private val errorHandler: ((error: Throwable, T) -> Unit)?
) {
    private val job = SupervisorJob()
    private val coroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext by lazy { job + dispatcher }
    }
    private var runningJob: SingleResultJob<R, T>? = null
    private val jobActor = coroutineScope.actor<Event<R, T>>(capacity = Channel.UNLIMITED) {
        channel.consumeEach { event -> handleEvent(event) }
    }
    private val jobCallback = object :
        SingleResultJob.Callback<R, T> {
        override fun onResult(job: SingleResultJob<R, T>, result: R, tag: T) {
            jobActor.offer(Event.succeed(job, result, tag))
        }

        override fun onFailure(job: SingleResultJob<R, T>, error: Throwable, tag: T) {
            jobActor.offer(Event.fail(job, error, tag))
        }
    }
    val isRunning: Boolean
        get() {
            return synchronized(this@SingleResultTask) {
                null != this.runningJob
            }
        }

    fun start(param: P, tag: T): Boolean =
        synchronized(this@SingleResultTask) {
            if (null == runningJob) {
                runningJob = creator(param, tag)
                runningJob!!.addCallback(jobCallback)
                true
            } else false
        }

    fun stop(): Boolean =
        synchronized(this@SingleResultTask) {
            runningJob?.let {
                it.removeCallback(jobCallback)
                it.cancel()
                runningJob = null
                true
            } ?: false
        }

    private fun handleEvent(event: Event<R, T>) {
        when (event.type) {
            Event.TYPE_SUCCEED -> {
                handleSucceed(event.job!!, event.result!!, event.tag!!)
            }
            Event.TYPE_FAIL -> {
                handleFail(event.job!!, event.error!!, event.tag!!)
            }
        }
    }

    private fun handleSucceed(job: SingleResultJob<R, T>, result: R, tag: T) =
        synchronized(this@SingleResultTask) {
            if (this.runningJob === job) {
                job.removeCallback(jobCallback)
                this.runningJob = null
                true
            } else false
        }.also { notifySucceed ->
            if (notifySucceed) {
                resultHandler?.invoke(result, tag)
            }
        }

    private fun handleFail(job: SingleResultJob<R, T>, error: Throwable, tag: T) =
        synchronized(this@SingleResultTask) {
            if (this.runningJob === job) {
                job.removeCallback(jobCallback)
                this.runningJob = null
                true
            } else false
        }.also { notifyFail ->
            if (notifyFail) {
                errorHandler?.invoke(error, tag)
            }
        }

    private data class Event<R : Any, T>(
        val type: Int,
        val job: SingleResultJob<R, T>?,
        val result: R? = null,
        val error: Throwable? = null,
        val tag: T? = null
    ) {
        companion object {
            const val TYPE_SUCCEED = 1
            const val TYPE_FAIL = 2

            fun <R : Any, T> succeed(
                job: SingleResultJob<R, T>,
                result: R,
                tag: T
            ): Event<R, T> =
                Event(
                    type = TYPE_SUCCEED,
                    job = job,
                    result = result,
                    tag = tag
                )

            fun <R : Any, T> fail(
                job: SingleResultJob<R, T>,
                error: Throwable,
                tag: T
            ): Event<R, T> =
                Event(
                    type = TYPE_FAIL,
                    job = job,
                    error = error,
                    tag = tag
                )
        }
    }
}