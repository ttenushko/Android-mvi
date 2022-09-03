package com.ttenushko.androidmvi.demo.presentation.screens.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.PlacesViewModel
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi.Store.*
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.findDependency
import com.ttenushko.androidmvi.demo.databinding.FragmentPlacesBinding
import com.ttenushko.androidmvi.demo.di.presentation.screen.DaggerPlacesFragmentComponent
import com.ttenushko.androidmvi.demo.di.presentation.screen.PlacesFragmentModule
import com.ttenushko.androidmvi.demo.presentation.base.fragment.BaseMviFragment
import com.ttenushko.androidmvi.demo.presentation.utils.PlaceAdapter
import com.ttenushko.androidmvi.demo.presentation.utils.isVisible
import com.ttenushko.mvi.android.MviStoreHolderViewModel
import javax.inject.Inject

class PlacesFragment :
    BaseMviFragment<Intent, State, Event>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var placeAdapter: PlaceAdapter? = null
    private var _viewBinding: FragmentPlacesBinding? = null
    private val viewBinding: FragmentPlacesBinding
        get() {
            return _viewBinding!!
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerPlacesFragmentComponent.builder()
            .dependencies(findDependency())
            .fragmentModule(PlacesFragmentModule(this))
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        FragmentPlacesBinding.inflate(inflater, container, false).let {
            _viewBinding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.layoutContent.btnAddPlace.setOnClickListener {
            dispatchMviIntent(Intent.AddPlaceButtonClicked)
        }
        placeAdapter = PlaceAdapter(
            requireContext(),
            object :
                PlaceAdapter.Callback {
                override fun onItemClicked(place: Place) {
                    dispatchMviIntent(Intent.PlaceClicked(place))
                }
            })
        viewBinding.layoutContent.placeList.adapter = placeAdapter
        viewBinding.layoutContent.placeList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        placeAdapter = null
        _viewBinding = null
    }

    override fun onStart() {
        super.onStart()
        toolbar?.title = "Places"
    }

    override fun onMviStateChanged(state: State) {
        val places = state.places
        viewBinding.layoutContent.root.isVisible = (null != state.places)
        viewBinding.layoutContent.layoutContentFilled.isVisible =
            (null != places && places.isNotEmpty())
        viewBinding.layoutContent.layoutContentEmpty.isVisible =
            (null != places && places.isEmpty())
        viewBinding.layoutError.root.isVisible = (null != state.error)
        viewBinding.layoutLoading.root.isVisible = state.isLoading
        if (null != state.places) {
            placeAdapter!!.set(state.places!!)
        } else {
            placeAdapter!!.clear()
        }
    }

    override fun onMviEvent(event: Event) {
    }

    override fun getViewModel(): MviStoreHolderViewModel<Intent, State, Event> =
        ViewModelProvider(this, viewModelFactory)[PlacesViewModel::class.java]
}
