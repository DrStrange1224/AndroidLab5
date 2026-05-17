package ru.android.origlab5.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import ru.android.origlab5.data.FlightData
import ru.android.origlab5.data.PreferencesManager
import ru.android.origlab5.data.dao.AirportDao
import ru.android.origlab5.data.dao.FavoriteDao
import ru.android.origlab5.data.entity.AirportEntity
import ru.android.origlab5.data.entity.FavoriteEntity

class FlightRepository(
    private val airportDao : AirportDao,
    private val favoriteDao : FavoriteDao,
    private val preferences : PreferencesManager
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

    suspend fun isFlightFavorite(from : AirportEntity, to : AirportEntity) : Boolean?{
        return (favoriteDao.getOne(from.iataCode, to.iataCode) != null)
    }

    suspend fun addFavoriteFlight(from : AirportEntity, to : AirportEntity){
        favoriteDao.add(
            FavoriteEntity(
                departureCode = from.iataCode,
                destinationCode = to.iataCode
            )
        )
    }

    suspend fun removeFavoriteFlight(from : AirportEntity, to : AirportEntity){
        favoriteDao.delete(
            from.iataCode,
            to.iataCode
        )
    }

    val lastSearchQuery: Flow<String?> = preferences.lastSearchQuery

    suspend fun saveLastSearchQuery(query: String) {
        return preferences.saveLastQuery(query)
    }

    suspend fun clearLastSearchQuery() {
        return preferences.clearLastQuery()
    }
}