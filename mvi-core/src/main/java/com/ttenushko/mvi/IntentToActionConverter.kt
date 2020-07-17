package com.ttenushko.mvi

interface IntentToActionConverter<I, A> {
    fun convert(intent: I): A
}