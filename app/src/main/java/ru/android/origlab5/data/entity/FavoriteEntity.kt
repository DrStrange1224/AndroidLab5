package ru.android.origlab5.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "airport")
data class FavoriteEntity (
    @PrimaryKey val id : Int,
    @ColumnInfo(name="departure_code") val departureCode : String,
    @ColumnInfo(name="destination_code") val destinationCode : String
)