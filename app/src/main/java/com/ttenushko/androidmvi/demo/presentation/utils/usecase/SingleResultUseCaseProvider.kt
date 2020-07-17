package com.ttenushko.androidmvi.demo.presentation.utils.usecase

import com.ttenushko.androidmvi.demo.domain.usecase.SingleResultUseCase

interface SingleResultUseCaseProvider<P : Any, R : Any, T> {
    fun provide(param: P, tag: T): SingleResultUseCase<P, R>
}

fun <P : Any, R : Any, T> singleResultUseCaseProvider(useCaseProvider: (P, T) -> SingleResultUseCase<P, R>): SingleResultUseCaseProvider<P, R, T> =
    object : SingleResultUseCaseProvider<P, R, T> {
        override fun provide(param: P, tag: T): SingleResultUseCase<P, R> {
            return useCaseProvider(param, tag)
        }
    }