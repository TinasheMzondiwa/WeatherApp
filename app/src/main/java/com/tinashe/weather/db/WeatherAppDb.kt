package com.tinashe.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tinashe.weather.db.dao.LocationDao
import com.tinashe.weather.db.dao.PlacesDao
import com.tinashe.weather.model.CurrentLocation
import com.tinashe.weather.model.SavedPlace

/**
 * Created by tinashe on 2018/03/21.
 */
@Database(entities = [(CurrentLocation::class), (SavedPlace::class)], version = 2, exportSchema = false)
@TypeConverters(DataTypeConverters::class)
abstract class WeatherAppDb : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun placesDao(): PlacesDao

    companion object {
        private const val DATABASE_NAME = "weather_db"

        fun create(context: Context): WeatherAppDb = Room.databaseBuilder(context, WeatherAppDb::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}