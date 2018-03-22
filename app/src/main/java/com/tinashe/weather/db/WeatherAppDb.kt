package com.tinashe.weather.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.tinashe.weather.db.dao.LocationDao
import com.tinashe.weather.model.CurrentLocation

/**
 * Created by tinashe on 2018/03/21.
 */
@Database(entities = [(CurrentLocation::class)], version = 1)
abstract class WeatherAppDb : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    companion object {
        const val DATABASE_NAME = "weather_db"
    }
}