package com.ttenushko.androidmvi.demo.presentation.screen.placedetails.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ttenushko.androidmvi.demo.R
import com.ttenushko.androidmvi.demo.common.presentation.screen.placedetails.mvi.Store.*
import com.ttenushko.androidmvi.demo.presentation.base.theme.AppTheme
import com.ttenushko.androidmvi.demo.presentation.widgets.CommonErrorDialog
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach

@Composable
fun PlaceDetailsView(
    state: State,
    events: ReceiveChannel<Event>,
    dispatchIntent: (Intent) -> Unit,
) {
    val isDeleteConfirmationDialogVisible = remember { mutableStateOf(false) }
    val error = remember { mutableStateOf<Throwable?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                modifier = Modifier,
                navigationIcon = {
                    IconButton(onClick = { dispatchIntent(Intent.ToolbarBackButtonClicked) }) {
                        Icon(Icons.Filled.ArrowBack, "")
                    }
                },
                title = {
                    Text(
                        text = state.weather?.let { "${it.place.name}, ${it.place.countyCode.uppercase()}" }
                            ?: ""
                    )
                },
                actions = {
                    Menu(state) { dispatchIntent(Intent.DeleteClicked) }
                }
            )
        }
    ) {
        Content(
            state = state,
            onRefresh = { dispatchIntent(Intent.Refresh) },
            modifier = Modifier.fillMaxSize()
        )
    }

    if (isDeleteConfirmationDialogVisible.value) {
        DeleteConfirmationDialog(
            onDismiss = { isDeleteConfirmationDialogVisible.value = false },
            positiveButtonClicked = {
                isDeleteConfirmationDialogVisible.value = true
                dispatchIntent(Intent.DeleteConfirmed)
            }
        )
    }

    error.value?.let { throwable ->
        CommonErrorDialog(
            error = throwable,
            onDismiss = { error.value = null }
        )
    }

    LaunchedEffect(events) {
        events.consumeEach { event ->
            when (event) {
                is Event.ShowDeleteConfirmation -> {
                    isDeleteConfirmationDialogVisible.value = true
                }
                is Event.ShowError -> {
                    error.value = event.error
                }
            }
        }
    }
}

@Composable
fun Menu(
    state: State,
    deleteClickHandler: () -> Unit,
) {
    Row {
        if (state.isDeleteButtonVisible) {
            IconButton(onClick = deleteClickHandler) {
                Icon(Icons.Filled.Delete, "")
            }
        }
    }
}

@Composable
fun Content(state: State, onRefresh: () -> Unit, modifier: Modifier = Modifier) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isRefreshing)
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        modifier = modifier,
        indicator = { indicatorState, trigger ->
            SwipeRefreshIndicator(
                state = indicatorState,
                refreshTriggerDistance = trigger,
                scale = true,
                backgroundColor = AppTheme.colors.materialColors.primary,
                shape = AppTheme.shapes.materialShapes.small,
            )
        }
    ) {
        if (null != state.weather) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                WeatherIconAndCurrentTemperature(
                    imageUrl = state.weather!!.descriptions.firstOrNull()?.iconUrl ?: "",
                    currentTemp = "${state.weather!!.conditions.tempCurrent.toInt()}\u2103"
                )
                WeatherCondition(
                    modifier = Modifier,
                    conditionName = stringResource(id = R.string.place_details_temp_min),
                    conditionValue = "${state.weather!!.conditions.tempMin.toInt()}\u2103"
                )
                WeatherCondition(
                    modifier = Modifier,
                    conditionName = stringResource(id = R.string.place_details_temp_max),
                    conditionValue = "${state.weather!!.conditions.tempMax.toInt()}\u2103"
                )
                WeatherCondition(
                    modifier = Modifier,
                    conditionName = stringResource(id = R.string.place_details_humidity),
                    conditionValue = "${state.weather!!.conditions.humidity}%"
                )
            }
        }
    }
}

@Composable
fun WeatherIconAndCurrentTemperature(
    imageUrl: String,
    currentTemp: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(72.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Image(
                painter = rememberImagePainter(
                    data = imageUrl,
                    builder = {
                        crossfade(true)
                        scale(Scale.FIT)
                    }
                ),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f, true)
                    .align(Alignment.CenterEnd)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = currentTemp,
            style = MaterialTheme.typography.h3,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
    }
}

@Composable
private fun WeatherCondition(modifier: Modifier, conditionName: String, conditionValue: String) {
    Row(
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
    ) {
        Text(
            text = conditionName,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .wrapContentSize(align = Alignment.CenterEnd)
                .weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = conditionValue,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .wrapContentSize(align = Alignment.CenterStart)
                .weight(1f)
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    positiveButtonClicked: () -> Unit,
) {
    AlertDialog(
        text = {
            Text(
                text = stringResource(id = R.string.delete_confirmation_message),
                color = AppTheme.colors.materialColors.onPrimary
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                colors = buttonColors(
                    backgroundColor = AppTheme.colors.materialColors.secondary,
                    contentColor = AppTheme.colors.materialColors.onSecondary,
                ),
                onClick = positiveButtonClicked
            ) {
                Text(stringResource(id = android.R.string.ok))
            }
        }
    )
}