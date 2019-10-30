package com.ttenushko.androidmvi.demo.presentation.screens.launcher

import android.os.Bundle
import com.ttenushko.androidmvi.demo.presentation.base.BaseActivity
import com.ttenushko.androidmvi.demo.presentation.screens.home.HomeActivity

class LauncherActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeActivity.launch(this)
    }
}