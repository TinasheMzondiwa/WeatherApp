package com.tinashe.weather.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.tinashe.weather.data.model.CurrentLocation
import io.reactivex.Maybe

/**
 * Created by tinashe on 2018/03/21.
 */
@Dao
interface LocationDao : BaseDao<CurrentLocation> {

    @Query("SELECT * FROM location LIMIT 1")
    fun getCurrentLocation(): Maybe<CurrentLocation>
}