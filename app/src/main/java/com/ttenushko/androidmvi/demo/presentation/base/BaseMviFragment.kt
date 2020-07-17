package com.ttenushko.androidmvi.demo.presentation.base

import android.os.Bundle
import androidx.lifecycle.Observer
import com.ttenushko.mvi.android.MviStoreViewModel

abstract class BaseMviFragment<I, S, E> : BaseFragment() {

    private lateinit var mviStoreViewModel: MviStoreViewModel<I, S, E>
    private val mviStoreStateObserver = Observer<S> { onMviStateChanged(it) }
    private val mviStoreEventObserver = Observer<E> { onMviEvent(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mviStoreViewModel = getMviStoreViewModel().apply { run(savedInstanceState) }
    }

    override fun onStart() {
        super.onStart()
        mviStoreViewModel.state.observe(viewLifecycleOwner, mviStoreStateObserver)
        mviStoreViewModel.events.observe(viewLifecycleOwner, mviStoreEventObserver)
    }

    override fun onStop() {
        super.onStop()
        mviStoreViewModel.state.removeObserver(mviStoreStateObserver)
        mviStoreViewModel.events.removeObserver(mviStoreEventObserver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mviStoreViewModel.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    protected fun dispatchMviIntent(intent: I) {
        mviStoreViewModel.dispatchIntent(intent)
    }

    protected abstract fun onMviStateChanged(state: S)

    protected abstract fun onMviEvent(event: E)

    protected abstract fun getMviStoreViewModel(): MviStoreViewModel<I, S, E>
}