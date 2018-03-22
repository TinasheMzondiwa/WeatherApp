package com.tinashe.weather.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.tinashe.weather.model.CurrentLocation.Companion.TABLE_NAME

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