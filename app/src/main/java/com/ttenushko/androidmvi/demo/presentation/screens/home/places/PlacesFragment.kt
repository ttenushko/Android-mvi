package com.ttenushko.androidmvi.demo.presentation.screens.home.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttenushko.androidmvi.MviStoreViewModel
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.presentation.base.BaseMviFragment
import com.ttenushko.androidmvi.demo.presentation.screens.home.Router
import com.ttenushko.androidmvi.demo.presentation.screens.home.common.PlaceAdapter
import com.ttenushko.androidmvi.demo.presentation.screens.home.places.mvi.PlacesStore.*
import com.ttenushko.androidmvi.demo.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.presentation.utils.isVisible
import kotlinx.android.synthetic.main.fragment_places.*
import kotlinx.android.synthetic.main.layout_places_content.*

class PlacesFragment :
    BaseMviFragment<Intention, State, Event>() {

    private val eventLogger = MviEventLogger<Event>("mvi")
    private var placeAdapter: PlaceAdapter? = null

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

    @Suppress("UNCHECKED_CAST")
    override fun getMviStoreViewModel(savedState: Bundle?):
            MviStoreViewModel<Intention, State, Event> {
        return ViewModelProviders.of(
            this,
            MviStoreViewModelProviderFactory(savedState)
        ).get(MviStoreViewModel::class.java) as MviStoreViewModel<Intention, State, Event>
    }

    @Suppress("UNCHECKED_CAST")
    class MviStoreViewModelProviderFactory(private val savedState: Bundle?) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MviStoreViewModel(PlacesFragmentStoreCreator(), savedState) as T
        }
    }
}
