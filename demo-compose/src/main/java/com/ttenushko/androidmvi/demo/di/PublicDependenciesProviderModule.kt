package com.ttenushko.androidmvi.demo.di

import com.ttenushko.androidmvi.demo.common.di.Dependency
import com.ttenushko.androidmvi.demo.common.di.DependencyKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class PublicDependenciesProviderModule {
    @Binds
    @IntoMap
    @DependencyKey(PublicDependencies::class)
    abstract fun publicDependencies(component: AppComponent): Dependency
}