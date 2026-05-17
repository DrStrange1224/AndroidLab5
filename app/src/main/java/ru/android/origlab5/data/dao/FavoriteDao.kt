package ru.android.origlab5.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.android.origlab5.data.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getAll() : Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorite WHERE departure_code = :fromCode AND destination_code = :toCode LIMIT 1")
    suspend fun getOne(fromCode : String, toCode : String) : FavoriteEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(favorite : FavoriteEntity)

    @Query("DELETE FROM favorite WHERE departure_code = :fromCode AND destination_code = :toCode")
    suspend fun delete(fromCode : String, toCode : String)
}