package com.ttenushko.androidmvi.demo.presentation.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.common.di.DependenciesProvider
import com.ttenushko.androidmvi.demo.common.di.ProvidesDependencies
import com.ttenushko.androidmvi.demo.common.presentation.base.router.Router
import com.ttenushko.androidmvi.demo.common.presentation.base.router.RouterProxy
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.findDependency
import com.ttenushko.androidmvi.demo.di.domain.UseCaseModule
import com.ttenushko.androidmvi.demo.di.presentation.screen.DaggerHomeActivityComponent
import com.ttenushko.androidmvi.demo.di.presentation.screen.HomeActivityModule
import com.ttenushko.androidmvi.demo.presentation.base.activity.BaseActivity
import javax.inject.Inject

class MainActivity : BaseActivity(),
    ProvidesDependencies {

    @Inject
    lateinit var router: Router<MainRouter.Destination>

    @Inject
    lateinit var routerProxy: RouterProxy<MainRouter.Destination>

    @Suppress("ProtectedInFinal")
    @Inject
    override lateinit var dependenciesProvider: DependenciesProvider
        protected set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerHomeActivityComponent.builder()
            .publicDependencies(findDependency())
            .activityModule(HomeActivityModule(this))
            .useCaseModule(UseCaseModule())
            .build()
            .inject(this)

        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        routerProxy.attach(router)
    }

    override fun onStop() {
        super.onStop()
        routerProxy.detach(router)
    }

    override fun onNavigationBackStackChanged() {
        super.onNavigationBackStackChanged()
        navigationTopmostFragment?.view?.findViewById<Toolbar>(R.id.toolbar)?.let { toolbar ->
            val navController = findNavController(R.id.nav_host_fragment)
            val appBarConfiguration = AppBarConfiguration(navController.graph)
            setSupportActionBar(toolbar)
            toolbar.setupWithNavController(navController, appBarConfiguration)
            setupActionBarWithNavController(navController)
        }
    }

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            }
            context.startActivity(intent)
        }
    }
}