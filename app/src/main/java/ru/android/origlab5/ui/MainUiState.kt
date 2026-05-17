package ru.android.origlab5.ui

import ru.android.origlab5.data.FlightData
import ru.android.origlab5.data.entity.AirportEntity
import ru.android.origlab5.data.entity.FavoriteEntity

data class MainUiState (
    val selectedAirport : AirportEntity? = null,
    val foundFlights : List<FlightData> = emptyList(),
    val suggestions : List<AirportEntity> = emptyList(),
    val isShowingSuggestions : Boolean = false,
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
    val favoriteFlights : List<FlightData> = emptyList()
)