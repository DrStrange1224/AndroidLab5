package ru.android.origlab5.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.android.origlab5.data.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getAll() : Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorite WHERE departure_code = :fromCode AND destination_code = :toCode LIMIT 1")
    fun getOne(fromCode : String, toCode : String) : FavoriteEntity?
}