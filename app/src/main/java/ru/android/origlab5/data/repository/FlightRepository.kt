package ru.android.origlab5.data.repository

import ru.android.origlab5.data.dao.AirportDao
import ru.android.origlab5.data.dao.FavoriteDao

class FlightRepository(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao,
) {

}