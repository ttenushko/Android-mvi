package com.ttenushko.androidmvi.demo.presentation.base.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.ttenushko.androidmvi.demo.R

open class BaseFragment : Fragment() {

    protected var toolbar: Toolbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toolbar = null
    }
}