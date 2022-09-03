package com.ttenushko.mvi

public typealias Dispatcher<T> = (value: T) -> Unit
public typealias Provider<T> = () -> T
