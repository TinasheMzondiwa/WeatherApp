package com.tinashe.weather.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by tinashe on 2018/03/21.
 */
@Entity(tableName = "location")
data class CurrentLocation(var name: String, var latLong: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}