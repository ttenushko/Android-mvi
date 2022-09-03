package com.ttenushko.androidmvi.demo.presentation.base.fragment

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ttenushko.mvi.android.MviStoreHolderViewModel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

abstract class BaseMviFragment<I, S, E> : BaseFragment() {

    private lateinit var viewModel: MviStoreHolderViewModel<I, S, E>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel().apply {
            if (!mviStore.isRunning) {
                mviStore.run()
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    onMviStateChanged(state)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.consumeAsFlow().collect { event ->
                    onMviEvent(event)
                }
            }
        }
    }

    protected fun dispatchMviIntent(intent: I) {
        viewModel.mviStore.dispatchIntent(intent)
    }

    protected abstract fun onMviStateChanged(state: S)

    protected abstract fun onMviEvent(event: E)

    protected abstract fun getViewModel(): MviStoreHolderViewModel<I, S, E>
}