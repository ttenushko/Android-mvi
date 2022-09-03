package com.ttenushko.androidmvi.demo.presentation.screens.addplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.AddPlaceViewModel
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.*
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.findDependency
import com.ttenushko.androidmvi.demo.databinding.FragmentAddPlaceBinding
import com.ttenushko.androidmvi.demo.di.presentation.screen.AddPlaceFragmentModule
import com.ttenushko.androidmvi.demo.di.presentation.screen.DaggerAddPlaceFragmentComponent
import com.ttenushko.androidmvi.demo.presentation.base.fragment.BaseMviFragment
import com.ttenushko.androidmvi.demo.presentation.utils.PlaceAdapter
import com.ttenushko.mvi.android.MviStoreHolderViewModel
import javax.inject.Inject


class AddPlaceFragment :
    BaseMviFragment<Intent, State, Event>() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var placeAdapter: PlaceAdapter? = null
    private var _viewBinding: FragmentAddPlaceBinding? = null
    private val viewBinding: FragmentAddPlaceBinding
        get() {
            return _viewBinding!!
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerAddPlaceFragmentComponent.builder()
            .dependencies(findDependency())
            .fragmentModule(AddPlaceFragmentModule(this,
                requireArguments().getString(ARG_SEARCH)!!))
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        FragmentAddPlaceBinding.inflate(inflater, container, false).let {
            _viewBinding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeAdapter = PlaceAdapter(
            requireContext(),
            object :
                PlaceAdapter.Callback {
                override fun onItemClicked(place: Place) {
                    dispatchMviIntent(Intent.PlaceClicked(place))
                }
            })
        viewBinding.placeList.apply {
            adapter = placeAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        viewBinding.toolbar.searchView.apply {
            isFocusable = true
            isIconified = false
            setOnQueryTextListener(searchTextWatcher)
            setIconifiedByDefault(false)
            //requestFocusFromTouch()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding.toolbar.searchView.setOnQueryTextListener(null)
        placeAdapter = null
        _viewBinding = null
    }

    override fun onStart() {
        super.onStart()
        toolbar?.title = "Add Place"
    }

    override fun onMviStateChanged(state: State) {
        if (state.search != viewBinding.toolbar.searchView.query.toString()) {
            viewBinding.toolbar.searchView.setOnQueryTextListener(null)
            viewBinding.toolbar.searchView.setQuery(state.search, true)
            viewBinding.toolbar.searchView.setOnQueryTextListener(searchTextWatcher)
        }
        when (val searchResult = state.searchResult) {
            is State.SearchResult.Success -> {
                placeAdapter!!.set(searchResult.places)
            }
            is State.SearchResult.Failure -> {
                placeAdapter!!.clear()
            }
        }
        viewBinding.progress.isVisible = state.isSearching
        when {
            state.isShowSearchPrompt -> {
                viewBinding.message.isVisible = true
                viewBinding.message.text = "Start typing text to search"
            }
            state.isShowSearchNoResultsPrompt -> {
                viewBinding.message.isVisible = true
                viewBinding.message.text = "Nothing found"
            }
            state.isShowSearchErrorPrompt -> {
                viewBinding.message.isVisible = true
                viewBinding.message.text = "Error occurred"
            }
            else -> {
                viewBinding.message.isVisible = false
            }
        }
    }

    override fun onMviEvent(event: Event) {
    }

    override fun getViewModel(): MviStoreHolderViewModel<Intent, State, Event> =
        ViewModelProvider(this, viewModelFactory)[AddPlaceViewModel::class.java]


    private val searchTextWatcher = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(text: String): Boolean {
            return false
        }

        override fun onQueryTextChange(text: String): Boolean {
            dispatchMviIntent(Intent.SearchChanged(text))
            return true
        }
    }


    companion object {
        private const val ARG_SEARCH = "search"
        fun args(search: String): Bundle =
            Bundle().apply {
                putString(ARG_SEARCH, search)
            }
    }
}