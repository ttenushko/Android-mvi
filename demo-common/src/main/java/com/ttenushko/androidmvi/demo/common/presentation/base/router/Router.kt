package com.ttenushko.androidmvi.demo.common.presentation.base.router

interface Router<D : Router.Destination> {
    fun navigateTo(destination: D)

    interface Destination
}