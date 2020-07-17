package com.ttenushko.mvi

internal class ThreadChecker(
    private val requiredThreadId: Long,
    private val errorMessage: String
) {
    constructor(errorMessage: String) : this(Thread.currentThread().id, errorMessage)

    @Suppress("ReplaceGuardClauseWithFunctionCall")
    fun check() {
        if (requiredThreadId != Thread.currentThread().id) {
            throw IllegalStateException(errorMessage)
        }
    }
}