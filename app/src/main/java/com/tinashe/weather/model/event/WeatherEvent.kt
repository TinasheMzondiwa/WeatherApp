package com.tinashe.weather.model.event

import com.tinashe.weather.model.Entry

data class WeatherEvent (val placeId: String, val entry: Entry)