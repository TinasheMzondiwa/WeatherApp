package com.tinashe.weather.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.tinashe.weather.model.CurrentLocation
import com.tinashe.weather.model.CurrentLocation.Companion.TABLE_NAME
import io.reactivex.Maybe

/**
 * Created by tinashe on 2018/03/21.
 */
@Dao
interface LocationDao : BaseDao<CurrentLocation> {

    @Query("SELECT * FROM $TABLE_NAME LIMIT 1")
    fun getCurrentLocation(): Maybe<CurrentLocation>
}