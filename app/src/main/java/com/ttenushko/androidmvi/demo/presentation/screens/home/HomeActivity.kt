package com.ttenushko.androidmvi.demo.presentation.screens.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.presentation.base.BaseActivity

class HomeActivity(private val router: RouterImpl = RouterImpl()) : BaseActivity(),
    Router by router {

    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, HomeActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        router.initialize(this)
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
}