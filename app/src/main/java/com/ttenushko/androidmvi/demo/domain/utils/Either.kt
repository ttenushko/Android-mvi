package com.ttenushko.androidmvi.demo.domain.utils

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinToString
import java.util.*

sealed class Either<A, B> {

    class Left<A, B>(val value: A) : Either<A, B>() {

        companion object {
            private val properties = arrayOf(Either.Left<*, *>::value)
        }

        override fun equals(other: Any?) =
            kotlinEquals(other = other, properties = properties)

        override fun toString() =
            kotlinToString(properties = properties)

        override fun hashCode() =
            Objects.hash(value)
    }

    class Right<A, B>(val value: B) : Either<A, B>() {

        companion object {
            private val properties = arrayOf(Either.Right<*, *>::value)
        }

        override fun equals(other: Any?) =
            kotlinEquals(other = other, properties = properties)

        override fun toString() =
            kotlinToString(properties = properties)

        override fun hashCode() =
            Objects.hash(value)
    }
}