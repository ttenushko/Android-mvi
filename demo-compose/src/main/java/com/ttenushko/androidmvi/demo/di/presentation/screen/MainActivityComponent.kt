package com.ttenushko.androidmvi.demo.di.presentation.screen

import com.ttenushko.androidmvi.demo.di.PublicDependencies
import com.ttenushko.androidmvi.demo.di.domain.UseCaseModule
import com.ttenushko.androidmvi.demo.presentation.screen.MainActivity
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.ActivityScope
import dagger.Component

@Component(
    dependencies = [
        PublicDependencies::class
    ],
    modules = [
        MainActivityModule::class,
        UseCaseModule::class
    ]
)
@ActivityScope
interface MainActivityComponent {

    @Component.Builder
    interface Builder {
        fun publicDependencies(publicComponents: PublicDependencies): Builder
        fun activityModule(module: MainActivityModule): Builder
        fun useCaseModule(module: UseCaseModule): Builder
        fun build(): MainActivityComponent
    }

    fun inject(activity: MainActivity)
}