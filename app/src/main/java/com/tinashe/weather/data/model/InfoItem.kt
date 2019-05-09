package com.tinashe.weather.data.model

data class InfoItem(val icon: Int, val title: String) {

    var circle = false

    var description: String? = null

    var link: String? = null
}