package com.ttenushko.androidmvi.demo.presentation.base.router

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.SavedStateRegistryOwner
import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.mvi.android.compose.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow

abstract class ComposeRouter<D : Router.Destination>(
    private val startDestination: String
) : Router<D> {

    private val navigationRequests = Channel<D>(capacity = Channel.UNLIMITED)

    @Composable
    fun Render() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = startDestination) {
            buildNavGraph(this)
        }
        LaunchedEffect(navController) {
            navigationRequests.consumeAsFlow()
                .collect { destination ->
                    navigate(navController, destination)
                }
        }
    }

    override fun navigateTo(destination: D) {
        navigationRequests.trySend(destination)
    }

    protected abstract fun buildNavGraph(
        navGraphBuilder: NavGraphBuilder
    )

    protected abstract fun navigate(navController: NavController, destination: D)
}

@Composable
inline fun <D : Router.Destination> ((SavedStateRegistryOwner, D) -> Screen).rememberScreen(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    vararg keys: Any?,
    destination: @DisallowComposableCalls () -> D
): Screen =
    remember(this, *keys) { this(savedStateRegistryOwner, destination()) }

@Composable
inline fun Render(screen: @Composable () -> Screen) {
    screen().Render()
}