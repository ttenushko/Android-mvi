package com.ttenushko.mvi.extra

import com.ttenushko.mvi.Dispatcher
import com.ttenushko.mvi.MviMiddleware
import com.ttenushko.mvi.MviMiddlewareImpl
import java.io.Closeable

public class MviPostProcessorMiddleware<A, S, E>(
    private val postProcessors: List<PostProcessor<A, S, E>>
) : MviMiddlewareImpl<A, S, E>() {

    public constructor(vararg postProcessors: PostProcessor<A, S, E>) :
            this(listOf(*postProcessors))

    override fun onApply(action: A, chain: MviMiddleware.Chain) {
        val oldState = state
        val newState = chain.proceed().let { state }
        postProcessors.forEach {
            it.process(
                action,
                oldState,
                newState,
                dispatchAction,
                dispatchEvent
            )
        }
    }

    override fun onClose() {
        postProcessors.forEach { it.close() }
    }

    public interface PostProcessor<A, S, E> : Closeable {
        public fun process(
            action: A,
            oldState: S,
            newState: S,
            dispatchAction: Dispatcher<A>,
            dispatchEvent: Dispatcher<E>
        )
    }
}