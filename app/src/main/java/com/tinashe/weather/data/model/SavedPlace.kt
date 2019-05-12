package com.tinashe.weather.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

@Entity(tableName = "places")
data class SavedPlace(

        @PrimaryKey
        val placeId: String,

        val name: String,

        val address: String,

        val latLng: LatLng
) {

    @Ignore
    var entry: Entry? = null

    @Ignore
    var photo: Bitmap? = null

    constructor(place: Place) : this(
            place.id!!, place.name.toString(), place.address ?: "", place.latLng!!)
}