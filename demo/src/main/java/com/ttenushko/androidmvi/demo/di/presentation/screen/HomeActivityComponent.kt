package com.ttenushko.androidmvi.demo.di.presentation.screen

import androidx.appcompat.app.AppCompatActivity
import com.ttenushko.androidmvi.demo.common.di.Dependency
import com.ttenushko.androidmvi.demo.common.di.DependencyKey
import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.ActivityScope
import com.ttenushko.androidmvi.demo.di.PublicDependencies
import com.ttenushko.androidmvi.demo.di.domain.UseCaseModule
import com.ttenushko.androidmvi.demo.presentation.screens.MainActivity
import com.ttenushko.androidmvi.demo.presentation.screens.MainRouterImpl
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Component(
    dependencies = [PublicDependencies::class],
    modules = [HomeActivityModule::class, ComponentDependenciesModule::class, UseCaseModule::class]
)
@ActivityScope
interface HomeActivityComponent : PlacesFragmentDependencies, PlaceDetailsFragmentDependencies,
    AddPlaceFragmentDependencies {

    @Component.Builder
    interface Builder {
        fun publicDependencies(publicDependencies: PublicDependencies): Builder
        fun activityModule(activityModule: HomeActivityModule): Builder
        fun useCaseModule(useCaseModule: UseCaseModule): Builder
        fun build(): HomeActivityComponent
    }

    fun inject(activity: MainActivity)
}

@dagger.Module
abstract class ComponentDependenciesModule {
    @Binds
    @IntoMap
    @DependencyKey(PlacesFragmentDependencies::class)
    abstract fun bindPlacesFragmentDependencies(component: HomeActivityComponent): Dependency

    @Binds
    @IntoMap
    @DependencyKey(PlaceDetailsFragmentDependencies::class)
    abstract fun bindPlaceDetailsFragmentDependencies(component: HomeActivityComponent): Dependency

    @Binds
    @IntoMap
    @DependencyKey(AddPlaceFragmentDependencies::class)
    abstract fun bindAddPlaceDependencies(component: HomeActivityComponent): Dependency
}

@Module
class HomeActivityModule(
    private val activity: AppCompatActivity,
) {
    @ActivityScope
    @Provides
    fun mainRouterImpl(): Router<MainRouter.Destination> =
        MainRouterImpl(activity)
}