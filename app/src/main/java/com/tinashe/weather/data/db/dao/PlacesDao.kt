package com.tinashe.weather.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.tinashe.weather.data.model.SavedPlace
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface PlacesDao : BaseDao<SavedPlace> {

    @Query("SELECT * FROM places")
    fun listAll(): Flowable<List<SavedPlace>>

    @Query("SELECT * FROM places WHERE placeId = :placeId")
    fun findPlace(placeId: String): Maybe<SavedPlace>
}