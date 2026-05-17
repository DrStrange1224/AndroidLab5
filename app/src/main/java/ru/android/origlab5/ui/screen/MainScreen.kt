package ru.android.origlab5.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import ru.android.origlab5.ui.viewmodel.MainViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.android.origlab5.R
import ru.android.origlab5.data.entity.AirportEntity

@Composable
fun MainScreen(viewModel : MainViewModel, modifier : Modifier) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(33.dp)
    ){
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        when{
            uiState.isLoading -> {
                Log.w("MYLOGGER", "Current state: LOADING")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Log.w("MYLOGGER", "Current state: ERROR")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        uiState.errorMessage!!,
                        modifier
                    )
                }
            }

            uiState.isShowingSuggestions -> {
                Log.w("MYLOGGER", "Current state: SUGGESTIONS")
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.suggestions){ airport ->
                        Suggestion(
                            airport,
                            onClick = { viewModel.selectSuggestion(airport) }
                        )
                        Spacer(
                            modifier=Modifier
                                .height(5.dp)
                        )
                    }
                }
            }

            uiState.selectedAirport != null -> {
                Log.w("MYLOGGER", "Current state: SELECTED AIRPORT")
                LazyColumn {
                    items(uiState.foundFlights){ flight ->
                        Log.w("MYLOGGER", "Going through foundFlights, current = $flight")
                        FlightBlock(
                            departure = flight.departure,
                            destination = flight.destination,
                            isFavorite = flight.isFavorite,
                            onFavoriteToggle = { viewModel.toggleFavorite(flight) }
                        )
                        Spacer(
                            modifier=Modifier
                                .height(10.dp)
                        )
                    }
                }
            }

            uiState.favoriteFlights.isNotEmpty() -> {
                Log.w("MYLOGGER", "Current state: FAVORITES")
                LazyColumn {
                    items(uiState.favoriteFlights){ fav ->
                        FlightBlock(
                            departure = fav.departure,
                            destination = fav.destination,
                            isFavorite = true,
                            onFavoriteToggle = { viewModel.removeFavorite(fav) }
                        )
                        Spacer(
                            modifier=Modifier
                                .height(10.dp)
                        )
                    }
                }
            }

            else -> {
                Log.w("MYLOGGER", "Current state: FAVORITES EMPTY")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Text(
                        stringResource(R.string.no_favorites_text)
                    )
                }
            }
        }
    }
}

@Composable
fun FlightBlock(
    departure : AirportEntity,
    destination : AirportEntity,
    isFavorite : Boolean,
    onFavoriteToggle : () -> Unit
){
    Log.w("MYLOGGER", "Creating flight block")
    var fav by remember { mutableStateOf(isFavorite) }
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxWidth()
            .padding(10.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(3f)
        ){
            Text(
                stringResource(R.string.depart_flight_text)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    departure.iataCode,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    departure.name,
                    textAlign = TextAlign.Right
                )
            }

            Text(
                stringResource(R.string.arrive_flight_text)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    destination.iataCode,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    destination.name,
                    textAlign = TextAlign.Right
                )
            }
        }

        IconButton(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            onClick = {
                fav = !fav
                onFavoriteToggle()
            }
        ){
            Icon(
                imageVector = if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterVertically),
                tint = if (fav) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun Suggestion(
    airport : AirportEntity,
    onClick : () -> Unit
){
    Row(
        modifier = Modifier
            .clickable(
                enabled = true,
                onClick = onClick
            )
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            airport.iataCode,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            airport.name,
            textAlign = TextAlign.Right
        )
    }
}