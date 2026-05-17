package ru.android.origlab5.data

import ru.android.origlab5.data.entity.AirportEntity

data class FlightData(
    val departure : AirportEntity,
    val destination : AirportEntity,
    val isFavorite : Boolean
)
