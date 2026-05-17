package ru.android.origlab5.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.android.origlab5.data.dao.AirportDao
import ru.android.origlab5.data.dao.FavoriteDao
import ru.android.origlab5.data.entity.AirportEntity
import ru.android.origlab5.data.entity.FavoriteEntity

@Database(entities = [AirportEntity::class, FavoriteEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun airportDao() : AirportDao
    abstract fun favoriteDao() : FavoriteDao
    companion object{

        @Volatile
        private var INSTANCE : AppDatabase? = null

        fun getInstance(context : Context) : AppDatabase{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flights.db"
                )
                    .createFromAsset("databases/flights.db")
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}