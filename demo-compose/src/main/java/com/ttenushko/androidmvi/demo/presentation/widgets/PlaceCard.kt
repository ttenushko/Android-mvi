package com.ttenushko.androidmvi.demo.presentation.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ttenushko.androidmvi.demo.common.domain.weather.model.Place
import com.ttenushko.androidmvi.demo.presentation.base.theme.AppTheme

@Composable
fun PlaceCards(
    modifier: Modifier,
    places: List<Place>,
    placeClickHandler: (Place) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(
            items = places,
            key = { _, place -> place.id }
        ) { index, place ->
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (0 == index) 8.dp else 16.dp)
            )
            PlaceCard(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                place,
                placeClickHandler
            )
            if (index == places.size - 1) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
            }
        }
    }
}

@Composable
fun PlaceCard(
    modifier: Modifier,
    place: Place,
    placeClickHandler: (Place) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colors.surface,
        border = BorderStroke(1.dp, AppTheme.colors.materialColors.secondaryVariant),
        elevation = 0.dp,
        modifier = modifier
            .clickable(onClick = { placeClickHandler(place) })
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "${place.name}, ${place.countyCode}",
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "%1\$.6f, %2\$.6f".format(place.location.latitude, place.location.longitude),
                style = MaterialTheme.typography.body2
            )
        }
    }
}