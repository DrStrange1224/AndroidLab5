package ru.android.origlab5.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.ViewModel
import ru.android.origlab5.ui.viewmodel.MainViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import ru.android.origlab5.data.entity.AirportEntity
import ru.android.origlab5.data.entity.FavoriteEntity

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(

    ){
        //INPUT TEXT FIELD
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )

        //MAIN CONTENT
        when{
            //DATA LOADING
            uiState.isLoading -> {
                Box(

                ){
                    CircularProgressIndicator()
                }
            }

            //ERROR MESSAGE
            uiState.errorMessage != null -> {
                Text(
                    uiState.errorMessage!!
                )
            }

            //SUGGESTIONS
            uiState.isShowingSuggestions -> {
                LazyColumn(

                ) {
                    items(uiState.suggestions){ airport ->
                        Suggestion(
                            airport,
                            onClick = { viewModel.selectSuggestion(airport) }
                        )
                    }
                }
            }

            //WHEN AIRPORT SELECTED FROM SUGGESTIONS
            uiState.selectedAirport != null -> {
                LazyColumn(

                ) {
                    items(uiState.foundDestinations){ dest ->
                        FlightBlock(
                            departure = uiState.selectedAirport!!,
                            destination = dest,
                            onFavoriteToggle = { viewModel.toggleFavorite(dest) }
                        )
                    }
                }
            }

            //FAVORITES
            else -> {
                LazyColumn(

                ) {
                    items(uiState.favoriteFlights){ fav ->
                        FlightBlock(
                            departure = fav.departure,
                            destination = fav.destination,
                            onFavoriteToggle = { viewModel.removeFavorite(fav) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlightBlock(
    departure : AirportEntity,
    destination : AirportEntity,
    onFavoriteToggle : () -> Unit
){
    Row(

    ){
        //TEXT COLUMN
        Column(

        ){
            Text(
                "depart"
            )
            Row(

            ){
                Text(
                    departure.iataCode
                )
                Text(
                    departure.name
                )
            }

            Text(
                "arrive"
            )
            Row(

            ){
                Text(
                    destination.iataCode
                )
                Text(
                    destination.name
                )
            }
        }

        //FAVORITE BUTTON
        IconButton(
            onClick = onFavoriteToggle
        ){
            //TODO create favorite icon
        }
    }
}

@Composable
fun Suggestion(
    airport : AirportEntity,
    onClick : () -> Unit
){
    Button(
        onClick = {}
    ){
        Row(

        ){
            Text(
                airport.iataCode
            )
            Text(
                airport.name
            )
        }
    }
}