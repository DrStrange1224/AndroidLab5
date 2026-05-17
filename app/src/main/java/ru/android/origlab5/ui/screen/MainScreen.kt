package ru.android.origlab5.ui.screen

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import ru.android.origlab5.data.entity.FavoriteEntity

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(){
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

//        if (uiState.isShowingSuggestions){
//            LazyColumn(
//
//            ) {
//                items(uiState.suggestions){ airport ->
//                    Button(
//                        onClick = {
//
//                        }
//                    ) {
//                        Row(
//
//                        ) {
//                            Text(
//                                airport.iataCode
//                            )
//                            Text(
//                                airport.name
//                            )
//                        }
//                    }
//                }
//            }
//        }

        //MAIN CONTENT
        when{
            //DATA LOADING
            uiState.isLoading -> {}

            //ERROR MESSAGE
            uiState.errorMessage != null -> {}

            //SUGGESTIONS
            uiState.isShowingSuggestions -> {
                LazyColumn(

                ) {
                    items(uiState.suggestions){ airport ->

                    }
                }
            }

            //WHEN DEPARTURE SELECTED
            uiState.selectedDeparture != null -> {}

            //FAVORITES
            else -> {
                LazyColumn(

                ) {
                    items(uiState.favoriteFlights){ fav ->
                        FavoriteBlock(
                            fav=fav,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteBlock(
    fav : FavoriteEntity,
    viewModel: MainViewModel
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
                    fav.departureCode
                )
                Text(
                    viewModel.getAirportNameByCode(fav.departureCode)
                )
            }

            Text(
                "arrive"
            )
            Row(

            ){
                Text(
                    fav.destinationCode
                )
                Text(
                    viewModel.getAirportNameByCode(fav.destinationCode)
                )
            }
        }

        //FAVORITE BUTTON
        IconButton(
            onClick = { viewModel.removeFavorite(fav) }
        ){
            //TODO create favorite icon
        }
    }
}