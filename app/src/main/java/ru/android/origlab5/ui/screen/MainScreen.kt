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
import androidx.lifecycle.ViewModel
import ru.android.origlab5.ui.viewmodel.MainViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.android.origlab5.R
import ru.android.origlab5.data.entity.AirportEntity
import ru.android.origlab5.data.entity.FavoriteEntity

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
        //INPUT TEXT FIELD
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

        //MAIN CONTENT
        when{
            //DATA LOADING
            uiState.isLoading -> {
                Log.w("MYLOGGER", "Current state: LOADING")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }

            //ERROR MESSAGE
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

            //SUGGESTIONS
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

            //WHEN AIRPORT SELECTED FROM SUGGESTIONS
            uiState.selectedAirport != null -> {
                Log.w("MYLOGGER", "Current state: SELECTED AIRPORT")
                LazyColumn(

                ) {
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

            //FAVORITES
            uiState.favoriteFlights.isNotEmpty() -> {
                Log.w("MYLOGGER", "Current state: FAVORITES")
                LazyColumn(

                ) {
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
                        "У вас нет избранных полетов" //TODO clear hardcode
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
    val fav by remember { mutableStateOf(isFavorite) }
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxWidth()
            .padding(10.dp)
    ){
        //TEXT COLUMN
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(3f)
        ){
            Text(
                "depart"
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
                "arrive"
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

        //FAVORITE BUTTON
        IconButton(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            onClick = onFavoriteToggle
        ){
            Icon(
                painter = painterResource(R.drawable.star),
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