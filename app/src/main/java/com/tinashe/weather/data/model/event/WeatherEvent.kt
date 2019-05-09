package com.tinashe.weather.data.model.event

import com.tinashe.weather.data.model.Entry

data class WeatherEvent (val placeId: String, val entry: Entry)