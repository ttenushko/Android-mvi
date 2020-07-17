package com.ttenushko.androidmvi.demo.presentation.utils.usecase

import com.ttenushko.androidmvi.demo.domain.usecase.MultiResultUseCase

interface MultiResultUseCaseProvider<P : Any, R : Any, T> {
    fun provide(param: P, tag: T): MultiResultUseCase<P, R>
}

fun <P : Any, R : Any, T> multiResultUseCaseProvider(useCaseProvider: (P, T) -> MultiResultUseCase<P, R>): MultiResultUseCaseProvider<P, R, T> =
    object : MultiResultUseCaseProvider<P, R, T> {
        override fun provide(param: P, tag: T): MultiResultUseCase<P, R> {
            return useCaseProvider(param, tag)
        }
    }