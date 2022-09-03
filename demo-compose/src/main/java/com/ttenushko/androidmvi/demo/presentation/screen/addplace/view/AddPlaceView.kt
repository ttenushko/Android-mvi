package com.ttenushko.androidmvi.demo.presentation.screen.addplace.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store
import com.ttenushko.androidmvi.demo.common.presentation.screen.addplace.mvi.Store.State
import com.ttenushko.androidmvi.demo.presentation.base.theme.AppTheme
import com.ttenushko.androidmvi.demo.presentation.widgets.PlaceCards
import com.ttenushko.androidmvi.demo.presentation.widgets.SmallProgress
import kotlinx.coroutines.channels.ReceiveChannel

@Composable
fun AddPlaceView(
    state: State,
    @Suppress("UNUSED_PARAMETER") events: ReceiveChannel<Store.Event>,
    dispatchIntent: (Store.Intent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                modifier = Modifier,
                title = {
                    SearchInput(
                        search = state.search,
                        searchChanged = { search -> dispatchIntent(Store.Intent.SearchChanged(search)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { dispatchIntent(Store.Intent.ToolbarBackButtonClicked) }) {
                        Icon(Icons.Filled.ArrowBack, "")
                    }
                }
            )
        }
    ) {
        Content(
            state = state,
            placeClickHandler = { place -> dispatchIntent(Store.Intent.PlaceClicked(place)) },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SearchInput(search: String, searchChanged: (String) -> Unit, modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }
    TextField(
        value = search,
        onValueChange = searchChanged,
        singleLine = true,
        modifier = modifier
            .focusRequester(focusRequester)
            .wrapContentHeight()
            .defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth,
                minHeight = 20.dp
            ),
        textStyle = AppTheme.typography.materialTypography.body1,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            backgroundColor = Color.Transparent,
            cursorColor = MaterialTheme.colors.onPrimary
        ),
        placeholder = {
            Text(
                text = stringResource(id = R.string.add_place_search_hint),
                style = AppTheme.typography.materialTypography.body1
            )
        }
    )
    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        onDispose { }
    }
}

@Composable
fun Content(state: State, placeClickHandler: (Place) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        PlaceCards(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            places = (state.searchResult as? State.SearchResult.Success)?.places ?: emptyList(),
            placeClickHandler = placeClickHandler
        )

        val textModifier = Modifier
            .padding(16.dp)
            .align(Alignment.TopCenter)
        val textStyle = AppTheme.typography.materialTypography.body1
        when {
            state.isShowSearchPrompt -> {
                Text(
                    text = stringResource(id = R.string.add_place_search_prompt),
                    modifier = textModifier,
                    style = textStyle
                )
            }
            state.isShowSearchNoResultsPrompt -> {
                Text(
                    text = stringResource(id = R.string.add_place_no_results_prompt),
                    modifier = textModifier,
                    style = textStyle
                )
            }
            state.isShowSearchErrorPrompt -> {
                Text(
                    text = stringResource(id = R.string.add_place_error_prompt),
                    modifier = textModifier,
                    style = textStyle
                )
            }
        }
        if (state.isSearching) {
            SmallProgress(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(8.dp)
            )
        }
    }
}
