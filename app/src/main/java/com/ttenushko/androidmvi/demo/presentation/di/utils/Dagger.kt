package com.ttenushko.androidmvi.demo.presentation.di.utils


import android.app.Activity
import androidx.fragment.app.Fragment
import com.ttenushko.androidmvi.demo.di.dependency.ComponentDependencies
import com.ttenushko.androidmvi.demo.di.dependency.ComponentDependenciesProvider
import com.ttenushko.androidmvi.demo.di.dependency.HasComponentDependencies

inline fun <reified T : ComponentDependencies> Activity.findComponentDependencies(): T =
    findComponentDependenciesProvider()[T::class.java] as T

inline fun <reified T : ComponentDependencies> Fragment.findComponentDependencies(): T =
    findComponentDependenciesProvider()[T::class.java] as T

fun Fragment.findComponentDependenciesProvider(componentClass: Class<out ComponentDependencies>? = null): ComponentDependenciesProvider {
    var fragment = this.parentFragment
    while (null != fragment) {
        if ((fragment is HasComponentDependencies) &&
            (null == componentClass || fragment.dependencies.containsKey(componentClass))
        ) {
            return fragment.dependencies
        }
        fragment = fragment.parentFragment
    }

    this.activity?.let { activity ->
        if ((activity is HasComponentDependencies) &&
            (null == componentClass || activity.dependencies.containsKey(componentClass))
        ) {
            return activity.dependencies
        }
    }

    this.activity?.application?.let { application ->
        if ((application is HasComponentDependencies) &&
            (null == componentClass || application.dependencies.containsKey(componentClass))
        ) {
            return application.dependencies
        }
    }

    throw IllegalStateException("Can not find suitable dagger provider for $this")
}

fun Activity.findComponentDependenciesProvider(componentClass: Class<out ComponentDependencies>? = null): ComponentDependenciesProvider {
    this.application.let { application ->
        if ((application is HasComponentDependencies) &&
            (null == componentClass || application.dependencies.containsKey(componentClass))
        ) {
            return application.dependencies
        }
    }
    throw IllegalStateException("Can not find suitable dagger provider for $this")
}

