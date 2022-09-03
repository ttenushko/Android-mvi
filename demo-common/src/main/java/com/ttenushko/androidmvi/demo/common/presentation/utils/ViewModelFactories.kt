package com.ttenushko.androidmvi.demo.common.presentation.utils

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner

fun <VM : ViewModel> SavedStateRegistryOwner.savedStateViewModelFactory(
    creator: (savedStateHandle: SavedStateHandle) -> VM,
): ViewModelProvider.Factory {
    return object : AbstractSavedStateViewModelFactory(this, null) {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(
            key: String,
            modelClass: Class<VM>,
            handle: SavedStateHandle,
        ): VM {
            return creator(handle) as VM
        }
    }
}

fun <VM : ViewModel> SavedStateRegistryOwner.savedStateViewModelFactory(
    creator: (key: String, modelClass: Class<out ViewModel>, savedStateHandle: SavedStateHandle) -> VM,
): ViewModelProvider.Factory {
    return object : AbstractSavedStateViewModelFactory(this, null) {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(
            key: String,
            modelClass: Class<VM>,
            handle: SavedStateHandle,
        ): VM {
            return creator(key, modelClass, handle) as VM
        }
    }
}