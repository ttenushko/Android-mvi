package com.ttenushko.androidmvi.demo.common.di

import dagger.MapKey
import kotlin.reflect.KClass

interface Dependency

typealias DependenciesProvider = Map<Class<out Dependency>, @JvmSuppressWildcards Dependency>

interface ProvidesDependencies {
    val dependenciesProvider: DependenciesProvider
}

@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class DependencyKey(val value: KClass<out Dependency>)