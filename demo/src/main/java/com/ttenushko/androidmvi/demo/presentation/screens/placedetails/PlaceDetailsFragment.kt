package com.ttenushko.androidmvi.demo.presentation.screens.placedetails

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.PlaceDetailsViewModel
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store.*
import com.ttenushko.androidmvi.demo.common.presentation.utils.dagger.findDependency
import com.ttenushko.androidmvi.demo.databinding.FragmentPlaceDetailsBinding
import com.ttenushko.androidmvi.demo.di.presentation.screen.DaggerPlaceDetailsFragmentComponent
import com.ttenushko.androidmvi.demo.di.presentation.screen.PlaceDetailsFragmentModule
import com.ttenushko.androidmvi.demo.presentation.base.DefaultErrorHandler
import com.ttenushko.androidmvi.demo.presentation.base.fragment.BaseMviFragment
import com.ttenushko.androidmvi.demo.presentation.dialogs.DialogFragmentClickListener
import com.ttenushko.androidmvi.demo.presentation.dialogs.SimpleDialogFragment
import com.ttenushko.androidmvi.demo.presentation.utils.ValueUpdater
import com.ttenushko.androidmvi.demo.presentation.utils.isDialogShown
import com.ttenushko.androidmvi.demo.presentation.utils.showDialog
import com.ttenushko.mvi.android.MviStoreHolderViewModel
import javax.inject.Inject

class PlaceDetailsFragment :
    BaseMviFragment<Intent, State, Event>(), DialogFragmentClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var picasso: Picasso
    private var menuItemDelete: MenuItem? = null
    private val menuItemDeleteVisibilityUpdater = ValueUpdater(false) { isVisible ->
        menuItemDelete?.isVisible = isVisible
    }
    private val weatherIconUrl = ValueUpdater("") { iconUrl ->
        if (iconUrl.isNotBlank()) {
            picasso.load(iconUrl).into(viewBinding.icon)
        } else {
            viewBinding.icon.setImageBitmap(null)
        }
    }
    private var _viewBinding: FragmentPlaceDetailsBinding? = null
    private val viewBinding: FragmentPlaceDetailsBinding
        get() {
            return _viewBinding!!
        }


    init {
        setHasOptionsMenu(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerPlaceDetailsFragmentComponent.builder()
            .dependencies(findDependency())
            .fragmentModule(PlaceDetailsFragmentModule(this,
                requireArguments().getLong(ARG_PLACE_ID)))
            .build()
            .inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        FragmentPlaceDetailsBinding.inflate(inflater, container, false).let {
            _viewBinding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherIconUrl.set("")
        viewBinding.pullRefreshLayout.setOnRefreshListener { dispatchMviIntent(Intent.Refresh) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    override fun onStart() {
        super.onStart()
        toolbar?.title = "Place Details"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_place_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
        menuItemDelete = menu.findItem(R.id.delete)
        menuItemDeleteVisibilityUpdater.forceUpdate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.delete -> {
                dispatchMviIntent(Intent.DeleteClicked)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    override fun onMviStateChanged(state: State) {
        val weather = state.weather
        when {
            null != state.error -> {
                viewBinding.layoutContent.visibility = View.GONE
                viewBinding.layoutError.visibility = View.VISIBLE
            }
            null != weather -> {
                viewBinding.layoutContent.visibility = View.VISIBLE
                viewBinding.layoutError.visibility = View.GONE
                viewBinding.place.text =
                    "${weather.place.name}, ${weather.place.countyCode.toUpperCase()}"
                viewBinding.temperature.text =
                    "${weather.conditions.tempCurrent.toInt()}\u2103"
                viewBinding.tempMin.text = "${weather.conditions.tempMin.toInt()}\u2103"
                viewBinding.tempMax.text = "${weather.conditions.tempMax.toInt()}\u2103"
                viewBinding.humidity.text = "${weather.conditions.humidity}%"
            }
            else -> {
                viewBinding.layoutContent.visibility = View.GONE
                viewBinding.layoutError.visibility = View.GONE
            }
        }
        viewBinding.pullRefreshLayout.isRefreshing = state.isRefreshing
        menuItemDeleteVisibilityUpdater.set(state.isDeleteButtonVisible)
        viewBinding.layoutProgress.root.visibility =
            if (state.isDeleting) View.VISIBLE else View.GONE
        weatherIconUrl.set(state.weather?.descriptions?.firstOrNull()?.iconUrl ?: "")
    }

    override fun onMviEvent(event: Event) {
        when (event) {
            is Event.ShowDeleteConfirmation -> {
                if (!childFragmentManager.isDialogShown(DLG_DELETE_CONFIRMATION)) {
                    childFragmentManager.showDialog(
                        SimpleDialogFragment.newInstance(
                            null,
                            "Are you sure to remove this place?",
                            "OK",
                            "Cancel"
                        ), DLG_DELETE_CONFIRMATION
                    )
                }
            }
            is Event.ShowError -> {
                DefaultErrorHandler.showError(this, null, event.error)
            }
        }
    }

    override fun onDialogFragmentClick(
        dialogFragment: DialogFragment,
        dialog: DialogInterface,
        which: Int,
    ) {
        when (dialogFragment.tag) {
            DLG_DELETE_CONFIRMATION -> {
                if (DialogInterface.BUTTON_POSITIVE == which) {
                    dispatchMviIntent(Intent.DeleteConfirmed)
                }
            }
        }
    }

    override fun getViewModel(): MviStoreHolderViewModel<Intent, State, Event> =
        ViewModelProvider(this, viewModelFactory)[PlaceDetailsViewModel::class.java]


    companion object {
        private const val ARG_PLACE_ID = "placeId"
        private const val DLG_DELETE_CONFIRMATION = "deleteConfirmation"

        fun args(placeId: Long): Bundle =
            Bundle().apply {
                putLong(ARG_PLACE_ID, placeId)
            }
    }
}