package com.ttenushko.mvi

class MviPostProcessorMiddleware<A, S, E>(
    private val postProcessors: List<PostProcessor<A, S, E>>
) : MviMiddleware<A, S, E> {

    private val closeHandler = CloseHandler {
        // do nothing
    }

    override fun apply(chain: MviMiddleware.Chain<A, S, E>) {
        closeHandler.checkNotClosed()
        val actionDispatcher = chain.actionDispatcher
        val eventDispatcher = chain.eventDispatcher
        val action = chain.action
        val oldState = chain.state
        val newState = chain.proceed().let { chain.state }
        postProcessors.forEach {
            it.process(
                action,
                oldState,
                newState,
                actionDispatcher,
                eventDispatcher
            )
        }
    }

    override fun close() {
        closeHandler.close()
    }

    interface PostProcessor<A, S, E> {
        fun process(
            action: A,
            oldState: S,
            newState: S,
            actionDispatcher: Dispatcher<A>,
            eventDispatcher: Dispatcher<E>
        )
    }
}