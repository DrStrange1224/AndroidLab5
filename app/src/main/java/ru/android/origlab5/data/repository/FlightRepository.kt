package ru.android.origlab5.data.repository

import kotlinx.coroutines.flow.Flow
import ru.android.origlab5.data.FlightData
import ru.android.origlab5.data.dao.AirportDao
import ru.android.origlab5.data.dao.FavoriteDao
import ru.android.origlab5.data.entity.AirportEntity
import ru.android.origlab5.data.entity.FavoriteEntity

class FlightRepository(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao,
) {
    fun searchAirports(query : String) : Flow<List<AirportEntity>>{
        return airportDao.searchAirports(query)
    }

    fun getDestinationsFrom(airport : AirportEntity) : Flow<List<AirportEntity>>{
        return airportDao.getAllExcept(airport.iataCode)
    }

    fun getFavoriteFlights() : Flow<List<FavoriteEntity>>{
        return favoriteDao.getAll()
    }

    suspend fun getAirportByIata(iataCode : String) : AirportEntity?{
        return airportDao.getByIata(iataCode)
    }
}