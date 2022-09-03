package com.ttenushko.mvi.extra

import com.ttenushko.mvi.CloseHandler
import com.ttenushko.mvi.Dispatcher

public class MviSimplePostProcessor<A, S, E>(
    private val block: (action: A, oldState: S, newState: S, dispatchAction: Dispatcher<A>, dispatchEvent: Dispatcher<E>) -> Unit
) : MviPostProcessorMiddleware.PostProcessor<A, S, E> {

    private val closeHandler = CloseHandler {
        // do nothing
    }

    override fun process(
        action: A,
        oldState: S,
        newState: S,
        dispatchAction: Dispatcher<A>,
        dispatchEvent: Dispatcher<E>
    ) {
        closeHandler.checkNotClosed()
        block(action, oldState, newState, dispatchAction, dispatchEvent)
    }

    override fun close() {
        closeHandler.close()
    }
}