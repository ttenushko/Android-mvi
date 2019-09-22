package com.ttenushko.androidmvi

interface Transformer<I, O> {
    fun transform(input: I): O
}