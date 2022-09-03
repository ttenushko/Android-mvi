package com.ttenushko.androidmvi.demo.presentation.base.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ttenushko.androidmvi.demo.R

open class BaseActivity : AppCompatActivity() {

    protected val navigationFragmentManager: FragmentManager?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager

    protected val navigationTopmostFragment: Fragment?
        get() = navigationFragmentManager?.findFragmentById(R.id.nav_host_fragment)

    override fun onStart() {
        super.onStart()
        navigationFragmentManager?.addOnBackStackChangedListener(navigationBackStackChangedListener)
        onNavigationBackStackChanged()
    }

    override fun onStop() {
        super.onStop()
        navigationFragmentManager?.removeOnBackStackChangedListener(
            navigationBackStackChangedListener
        )
    }

    protected open fun onNavigationBackStackChanged() {

    }

    private val navigationBackStackChangedListener =
        FragmentManager.OnBackStackChangedListener {
            onNavigationBackStackChanged()
        }
}