package com.ttenushko.androidmvi.demo.presentation.screen.places.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Location
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi.Store
import com.ttenushko.androidmvi.demo.common.presentation.screen.places.mvi.Store.State
import com.ttenushko.androidmvi.demo.presentation.base.theme.AppTheme
import com.ttenushko.androidmvi.demo.presentation.widgets.PlaceCards
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

@Composable
fun PlacesView(
    state: State,
    @Suppress("UNUSED_PARAMETER") events: ReceiveChannel<Store.Event>,
    dispatchIntent: (Store.Intent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.places_title))
                },
                elevation = 0.dp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { dispatchIntent(Store.Intent.AddPlaceButtonClicked) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_24dp),
                    contentDescription = null
                )
            }
        }
    ) {
        PlaceViewContent(
            state = state,
            placeClickHandler = { place -> dispatchIntent(Store.Intent.PlaceClicked(place)) },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PlaceViewContent(
    state: State,
    placeClickHandler: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    if (null == state.error) {
        DataContent(
            places = state.places ?: listOf(),
            placeClickHandler = placeClickHandler,
            modifier = modifier
        )
    } else {
        ErrorContent(
            error = state.error,
            modifier = modifier
        )
    }
}

@Composable
private fun DataContent(
    places: List<Place>,
    placeClickHandler: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        PlaceCards(
            modifier = Modifier
                .fillMaxSize(),
            places = places,
            placeClickHandler = placeClickHandler
        )
    }
}

@Composable
private fun ErrorContent(
    @Suppress("UNUSED_PARAMETER") error: Throwable?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 16.dp, top = 32.dp, end = 16.dp, bottom = 16.dp),
            text = stringResource(id = R.string.error_unknown),
            textAlign = TextAlign.Center,
            style = AppTheme.typography.materialTypography.body1
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewContent() {
    AppTheme {
        PlacesView(
            state = State(
                places = listOf(
                    Place(id = 1, name = "Place1", countyCode = "AB", location = Location(0f, 0f)),
                    Place(id = 2, name = "Place2", countyCode = "CD", location = Location(0f, 0f)),
                ),
                error = null
            ),
            events = Channel { },
            dispatchIntent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewError() {
    AppTheme {
        PlacesView(
            state = State(
                places = null,
                error = IllegalStateException(),
            ),
            events = Channel { },
            dispatchIntent = {}
        )
    }
}