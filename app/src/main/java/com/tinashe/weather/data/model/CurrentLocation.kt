package com.tinashe.weather.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tinashe.weather.data.model.CurrentLocation.Companion.TABLE_NAME

/**
 * Created by tinashe on 2018/03/21.
 */
@Entity(tableName = TABLE_NAME)
data class CurrentLocation(var name: String, var latLong: String) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    companion object {
        const val TABLE_NAME = "location"
    }
}