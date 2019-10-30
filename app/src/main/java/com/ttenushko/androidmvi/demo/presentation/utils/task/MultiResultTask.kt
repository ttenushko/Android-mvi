package com.ttenushko.androidmvi.demo.presentation.utils.task

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

class MultiResultTask<P : Any, R : Any, T>(
    val name: String,
    private val dispatcher: CoroutineDispatcher,
    private val creator: (P, T) -> MultiResultJob<R, T>,
    private val resultHandler: ((R, T) -> Unit)?,
    private val errorHandler: ((error: Throwable, T) -> Unit)?,
    private val completeHandler: ((T) -> Unit)? = null
) {
    private val job = SupervisorJob()
    private val coroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext by lazy { job + dispatcher }
    }
    private var runningJob: MultiResultJob<R, T>? = null
    private val jobActor = coroutineScope.actor<Event<R, T>>(capacity = Channel.UNLIMITED) {
        channel.consumeEach { event -> handleEvent(event) }
    }
    private val jobCallback = object :
        MultiResultJob.Callback<R, T> {
        override fun onResult(job: MultiResultJob<R, T>, result: R, tag: T) {
            jobActor.offer(Event.result(job, result, tag))
        }

        override fun onComplete(job: MultiResultJob<R, T>, tag: T) {
            jobActor.offer(Event.succeed(job, tag))
        }

        override fun onFailure(job: MultiResultJob<R, T>, error: Throwable, tag: T) {
            jobActor.offer(Event.fail(job, error, tag))
        }
    }
    val isRunning: Boolean
        get() {
            return synchronized(this@MultiResultTask) {
                null != this.runningJob
            }
        }

    fun start(param: P, tag: T): Boolean =
        synchronized(this@MultiResultTask) {
            if (null == runningJob) {
                runningJob = creator(param, tag)
                runningJob!!.addCallback(jobCallback)
                true
            } else false
        }

    fun stop(): Boolean =
        synchronized(this@MultiResultTask) {
            runningJob?.let {
                it.removeCallback(jobCallback)
                it.cancel()
                runningJob = null
                true
            } ?: false
        }

    private fun handleEvent(event: Event<R, T>) {
        when (event.type) {
            Event.TYPE_RESULT -> {
                handleResult(event.job!!, event.result!!, event.tag!!)
            }
            Event.TYPE_SUCCEED -> {
                handleSucceed(event.job!!, event.tag!!)
            }
            Event.TYPE_FAIL -> {
                handleFail(event.job!!, event.error!!, event.tag!!)
            }
        }
    }

    private fun handleResult(job: MultiResultJob<R, T>, result: R, tag: T) =
        synchronized(this@MultiResultTask) {
            this.runningJob === job
        }.also { notifyResult ->
            if (notifyResult) {
                resultHandler?.invoke(result, tag)
            }
        }

    private fun handleSucceed(job: MultiResultJob<R, T>, tag: T) =
        synchronized(this@MultiResultTask) {
            if (this.runningJob === job) {
                job.removeCallback(jobCallback)
                this.runningJob = null
                true
            } else false
        }.also { notifySucceed ->
            if (notifySucceed) {
                completeHandler?.invoke(tag)
            }
        }

    private fun handleFail(job: MultiResultJob<R, T>, error: Throwable, tag: T) =
        synchronized(this@MultiResultTask) {
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
        val job: MultiResultJob<R, T>?,
        val result: R? = null,
        val error: Throwable? = null,
        val tag: T? = null
    ) {
        companion object {
            const val TYPE_RESULT = 1
            const val TYPE_SUCCEED = 2
            const val TYPE_FAIL = 3

            fun <R : Any, T> result(
                job: MultiResultJob<R, T>,
                result: R,
                tag: T
            ): Event<R, T> =
                Event(
                    type = TYPE_RESULT,
                    job = job,
                    result = result,
                    tag = tag
                )

            fun <R : Any, T> succeed(
                job: MultiResultJob<R, T>,
                tag: T
            ): Event<R, T> =
                Event(
                    type = TYPE_SUCCEED,
                    job = job,
                    tag = tag
                )

            fun <R : Any, T> fail(
                job: MultiResultJob<R, T>,
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