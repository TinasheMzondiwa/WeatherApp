package com.tinashe.weather.db

import android.arch.persistence.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

class DataTypeConverters {

    private val lock = Any()

    private var gson: Gson? = null

    private fun getGson(): Gson {
        synchronized(lock) {
            if (gson == null) {
                gson = Gson()
            }

            return gson as Gson
        }
    }

    @TypeConverter
    fun latLngToString(latLng: LatLng): String {
        return getGson().toJson(latLng)
    }

    @TypeConverter
    fun stringToLatLng(jsonString: String): LatLng {
        return getGson().fromJson(jsonString, LatLng::class.java)
    }

}