package ru.android.origlab5.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.android.origlab5.data.entity.AirportEntity

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport WHERE iata_code LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchAirports(query : String) : Flow<List<AirportEntity>>

    @Query("SELECT * FROM airport WHERE iata_code != :iataCode")
    fun getAllExcept(iataCode : String) : Flow<List<AirportEntity>>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode LIMIT 1")
    suspend fun getByIata(iataCode : String) : AirportEntity?
}