package com.ttenushko.androidmvi.demo.presentation.base

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

    protected fun <T> getTarget(clazz: Class<T>): T? =
        arrayOf(targetFragment, parentFragment, activity)
            .firstOrNull { null != it && clazz.isInstance(it) }
            ?.let { target -> clazz.cast(target) }
}