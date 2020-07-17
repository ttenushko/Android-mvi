package com.ttenushko.androidmvi.demo.presentation.screens.home.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.presentation.base.BaseMviFragment
import com.ttenushko.androidmvi.demo.presentation.di.utils.findComponentDependencies
import com.ttenushko.androidmvi.demo.presentation.screens.home.Router
import com.ttenushko.androidmvi.demo.presentation.screens.home.common.PlaceAdapter
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.di.DaggerPlacesFragmentComponent
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.PlacesStore.*
import com.ttenushko.androidmvi.demo.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.presentation.utils.isVisible
import com.ttenushko.mvi.android.MviStoreViewModel
import kotlinx.android.synthetic.main.fragment_places.*
import kotlinx.android.synthetic.main.layout_places_content.*
import javax.inject.Inject

class PlacesFragment :
    BaseMviFragment<Intention, State, Event>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var eventLogger: MviEventLogger<Any>
    private var placeAdapter: PlaceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerPlacesFragmentComponent.builder()
            .placesFragmentDependencies(findComponentDependencies())
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_places, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAddPlace.setOnClickListener {
            dispatchMviIntent(Intention.AddPlaceButtonClicked)
        }
        placeAdapter = PlaceAdapter(
            context!!,
            object :
                PlaceAdapter.Callback {
                override fun onItemClicked(place: Place) {
                    dispatchMviIntent(Intention.PlaceClicked(place))
                }
            })
        placeList.adapter = placeAdapter
        placeList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        placeAdapter = null
    }

    override fun onStart() {
        super.onStart()
        toolbar?.title = "Places"
    }

    override fun onMviStateChanged(state: State) {
        layoutContent.isVisible = (null != state.places)
        layoutContentFilled.isVisible = (null != state.places && state.places.isNotEmpty())
        layoutContentEmpty.isVisible = (null != state.places && state.places.isEmpty())
        layoutError.isVisible = (null != state.error)
        layoutLoading.isVisible = state.isLoading
        if (null != state.places) {
            placeAdapter!!.set(state.places)
        } else {
            placeAdapter!!.clear()
        }
    }

    override fun onMviEvent(event: Event) {
        eventLogger.log(event)
        when (event) {
            is Event.Navigation -> {
                getTarget(Router::class.java)?.navigateTo(event.destination)
            }
        }
    }

    override fun getMviStoreViewModel(): MviStoreViewModel<Intention, State, Event> =
        ViewModelProviders.of(this, viewModelFactory)[PlacesFragmentViewModel::class.java]
}
