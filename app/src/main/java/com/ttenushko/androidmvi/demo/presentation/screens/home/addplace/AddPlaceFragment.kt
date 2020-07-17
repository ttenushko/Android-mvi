package com.ttenushko.androidmvi.demo.presentation.screens.home.addplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.presentation.base.BaseMviFragment
import com.ttenushko.androidmvi.demo.presentation.di.utils.findComponentDependencies
import com.ttenushko.androidmvi.demo.presentation.screens.home.Router
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.di.AddPlaceFragmentModule
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.di.DaggerAddPlaceFragmentComponent
import com.ttenushko.androidmvi.demo.presentation.screens.home.addplace.mvi.AddPlaceStore.*
import com.ttenushko.androidmvi.demo.presentation.screens.home.common.PlaceAdapter
import com.ttenushko.androidmvi.demo.presentation.utils.MviEventLogger
import com.ttenushko.androidmvi.demo.presentation.utils.isVisible
import com.ttenushko.mvi.android.MviStoreViewModel
import kotlinx.android.synthetic.main.fragment_add_place.*
import kotlinx.android.synthetic.main.toolbar_with_search.*
import javax.inject.Inject


class AddPlaceFragment :
    BaseMviFragment<Intention, State, Event>() {

    companion object {
        private const val ARG_SEARCH = "search"
        fun args(search: String): Bundle =
            Bundle().apply {
                putString(ARG_SEARCH, search)
            }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var eventLogger: MviEventLogger<Any>
    private var placeAdapter: PlaceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerAddPlaceFragmentComponent.builder()
            .addPlaceFragmentDependencies(findComponentDependencies())
            .addPlaceFragmentModule(AddPlaceFragmentModule(arguments!!.getString(ARG_SEARCH)!!))
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_add_place, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeAdapter = PlaceAdapter(
            context!!,
            object :
                PlaceAdapter.Callback {
                override fun onItemClicked(place: Place) {
                    dispatchMviIntent(Intention.PlaceClicked(place))
                }
            })
        placeList.apply {
            adapter = placeAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        searchView.apply {
            isFocusable = true
            isIconified = false
            setOnQueryTextListener(searchTextWatcher)
            setIconifiedByDefault(false)
            requestFocusFromTouch()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
        placeAdapter = null
    }

    override fun onStart() {
        super.onStart()
        toolbar?.title = "Add Place"
    }

    override fun onMviStateChanged(state: State) {
        if (state.search != searchView.query.toString()) {
            searchView.setOnQueryTextListener(null)
            searchView.setQuery(state.search, true)
            searchView.setOnQueryTextListener(searchTextWatcher)
        }
        when (val searchResult = state.searchResult) {
            is State.SearchResult.Success -> {
                placeAdapter!!.set(searchResult.places)
            }
            is State.SearchResult.Failure -> {
                placeAdapter!!.clear()
            }
        }
        progress.isVisible = state.isSearching
        when {
            state.isShowSearchPrompt -> {
                message.isVisible = true
                message.text = "Start typing text to search"
            }
            state.isShowSearchNoResultsPrompt -> {
                message.isVisible = true
                message.text = "Nothing found"
            }
            state.isShowSearchErrorPrompt -> {
                message.isVisible = true
                message.text = "Error occurred"
            }
            else -> {
                message.isVisible = false
            }
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
        ViewModelProviders.of(this, viewModelFactory)[AddPlacesFragmentViewModel::class.java]

    private val searchTextWatcher = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(text: String): Boolean {
            return false
        }

        override fun onQueryTextChange(text: String): Boolean {
            dispatchMviIntent(Intention.SearchChanged(text))
            return true
        }
    }
}