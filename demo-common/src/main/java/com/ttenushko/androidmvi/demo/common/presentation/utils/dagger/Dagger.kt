package com.ttenushko.androidmvi.demo.common.presentation.utils.dagger


import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import androidx.fragment.app.Fragment
import com.ttenushko.androidmvi.demo.common.di.DependenciesProvider
import com.ttenushko.androidmvi.demo.common.di.Dependency
import com.ttenushko.androidmvi.demo.common.di.ProvidesDependencies


inline fun <reified T : Dependency> Activity.findDependency(): T =
    findDependenciesProvider()[T::class.java] as T

inline fun <reified T : Dependency> Fragment.findDependency(): T =
    findDependenciesProvider()[T::class.java] as T

inline fun <reified T : Dependency> BroadcastReceiver.findDependency(context: Context): T =
    findDependenciesProvider(context)[T::class.java] as T

inline fun <reified T : Dependency> Service.findDependency(): T =
    findDependenciesProvider()[T::class.java] as T

fun Fragment.findDependenciesProvider(componentClass: Class<out Dependency>? = null): DependenciesProvider {
    var fragment = this.parentFragment
    while (null != fragment) {
        if ((fragment is ProvidesDependencies) &&
            (null == componentClass || fragment.dependenciesProvider.containsKey(componentClass))
        ) {
            return fragment.dependenciesProvider
        }
        fragment = fragment.parentFragment
    }

    this.activity?.let { activity ->
        if ((activity is ProvidesDependencies) &&
            (null == componentClass || activity.dependenciesProvider.containsKey(componentClass))
        ) {
            return activity.dependenciesProvider
        }
    }

    this.activity?.application?.let { application ->
        if ((application is ProvidesDependencies) &&
            (null == componentClass || application.dependenciesProvider.containsKey(componentClass))
        ) {
            return application.dependenciesProvider
        }
    }

    throw IllegalStateException("Can not find suitable dagger provider for $this")
}

fun Activity.findDependenciesProvider(componentClass: Class<out Dependency>? = null): DependenciesProvider {
    this.application.let { application ->
        if ((application is ProvidesDependencies) &&
            (null == componentClass || application.dependenciesProvider.containsKey(componentClass))
        ) {
            return application.dependenciesProvider
        }
    }
    throw IllegalStateException("Can not find suitable dagger provider for $this")
}

fun BroadcastReceiver.findDependenciesProvider(
    context: Context,
    componentClass: Class<out Dependency>? = null
): DependenciesProvider {
    context.applicationContext.let { applicationContext ->
        if ((applicationContext is ProvidesDependencies) &&
            (null == componentClass ||
                    applicationContext.dependenciesProvider.containsKey(componentClass))
        ) {
            return applicationContext.dependenciesProvider
        }
    }
    throw IllegalStateException("Can not find suitable dagger provider for $this")
}

fun Service.findDependenciesProvider(
    componentClass: Class<out Dependency>? = null
): DependenciesProvider {
    applicationContext.let { applicationContext ->
        if ((applicationContext is ProvidesDependencies) &&
            (null == componentClass ||
                    applicationContext.dependenciesProvider.containsKey(componentClass))
        ) {
            return applicationContext.dependenciesProvider
        }
    }
    throw IllegalStateException("Can not find suitable dagger provider for $this")
}