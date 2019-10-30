package com.ttenushko.androidmvi

import android.os.Bundle

interface MviStoreCreator<I, S, E> {
    fun createMviStore(savedState: Bundle?): MviStore<I, S, E>
    fun saveState(mviStore: MviStore<I, S, E>, outState: Bundle)
}