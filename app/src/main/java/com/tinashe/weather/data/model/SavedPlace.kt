package com.tinashe.weather.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "places")
data class SavedPlace(@PrimaryKey val placeId: String) {

    var name: String? = null

    var address: String? = ""

    var latLng: LatLng? = null

    @Ignore
    var entry: Entry? = null

    @Ignore
    var photo: Bitmap? = null

    constructor(place: Place) : this(place.id) {
        this.name = place.name.toString()
        this.address = place.address?.toString()
        this.latLng = place.latLng
    }

    override fun toString(): String {
        return "SavedPlace(placeId='$placeId', entry=$entry)"
    }


}