package com.ttenushko.androidmvi.demo.presentation.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import coil.ImageLoader
import coil.compose.LocalImageLoader
import com.ttenushko.androidmvi.demo.di.domain.UseCaseModule
import com.ttenushko.androidmvi.demo.di.presentation.screen.MainActivityModule
import com.ttenushko.androidmvi.demo.presentation.base.activity.BaseActivity
import com.ttenushko.androidmvi.demo.common.presentation.base.router.RouterProxy
import com.ttenushko.androidmvi.demo.common.presentation.screen.MainRouter
import com.ttenushko.androidmvi.demo.presentation.base.theme.AppTheme
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.findDependency
import com.ttenushko.androidmvi.demo.di.presentation.screen.DaggerMainActivityComponent
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var router: MainRouterImpl

    @Inject
    lateinit var routerProxy: RouterProxy<MainRouter.Destination>

    @Inject
    lateinit var imageLoader: ImageLoader


    override fun onCreate(savedInstanceState: Bundle?) {

        DaggerMainActivityComponent.builder()
            .publicDependencies(findDependency())
            .activityModule(MainActivityModule(this))
            .useCaseModule(UseCaseModule())
            .build()
            .inject(this)

        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                CompositionLocalProvider(
                    LocalImageLoader provides imageLoader
                ) {
                    Surface(
                        color = AppTheme.colors.materialColors.background
                    ) {
                        router.Render()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        routerProxy.attach(router)
    }

    override fun onStop() {
        super.onStop()
        routerProxy.detach(router)
    }
}