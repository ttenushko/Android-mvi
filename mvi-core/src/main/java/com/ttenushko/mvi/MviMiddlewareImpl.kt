package com.ttenushko.mvi


public abstract class MviMiddlewareImpl<A, S, E> :
    MviMiddleware<A, S, E> {

    private var provideStateInt: Provider<S>? = null
    private var dispatchActionInt: Dispatcher<A>? = null
    private var dispatchEventInt: Dispatcher<E>? = null
    protected val state: S
        get() {
            return requireNotNull(provideStateInt) { "State provider is not set." }()
        }
    protected val dispatchAction: Dispatcher<A>
        get() {
            return requireNotNull(dispatchActionInt) { "Action dispatcher is not set." }
        }
    protected val dispatchEvent: Dispatcher<E>
        get() {
            return requireNotNull(dispatchEventInt) { "Event dispatcher is not set." }
        }
    private val closeHandler = CloseHandler {
        onClose()
    }

    final override fun setup(
        provideState: Provider<S>,
        dispatchAction: Dispatcher<A>,
        dispatchEvent: Dispatcher<E>
    ) {
        closeHandler.checkNotClosed()
        if (null == provideStateInt && null == dispatchActionInt && null == dispatchEventInt) {
            this.provideStateInt = provideState
            this.dispatchActionInt = dispatchAction
            this.dispatchEventInt = dispatchEvent
        } else throw IllegalStateException("Setup() is called multiple times.")
    }

    final override fun apply(action: A, chain: MviMiddleware.Chain) {
        closeHandler.checkNotClosed()
        onApply(action, chain)
    }

    final override fun close() {
        closeHandler.close()
    }

    protected abstract fun onApply(action: A, chain: MviMiddleware.Chain)

    protected abstract fun onClose()
}