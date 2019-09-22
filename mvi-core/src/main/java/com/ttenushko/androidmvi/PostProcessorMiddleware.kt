package com.ttenushko.androidmvi

class MviPostProcessorMiddleware<A, S, E>(
    private val postProcessors: List<PostProcessor<A, S, E>>
) : MviMiddleware<A, S, E> {

    override fun apply(chain: MviMiddleware.Chain<A, S, E>) {
        val actionDispatcher = chain.actionDispatcher
        val eventDispatcher = chain.eventDispatcher
        val action = chain.action
        val oldState = chain.stateProvider.get()
        val newState = chain.proceed().let { chain.stateProvider.get() }
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
        //do nothing
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