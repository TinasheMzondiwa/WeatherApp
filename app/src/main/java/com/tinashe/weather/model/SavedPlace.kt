package com.tinashe.weather.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap
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