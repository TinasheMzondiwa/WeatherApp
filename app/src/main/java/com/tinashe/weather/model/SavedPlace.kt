package com.tinashe.weather.model

import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng

data class SavedPlace(val placeId: String) {

    var name: CharSequence? = null

    var address: CharSequence? = ""

    var latLng: LatLng? = null

    constructor(place: Place): this(place.id){
        this.name = place.name.toString()
        this.address = place.address
        this.latLng = place.latLng
    }

}