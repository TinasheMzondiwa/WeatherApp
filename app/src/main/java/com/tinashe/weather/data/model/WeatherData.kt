package com.tinashe.weather.data.model

/**
 * Created by tinashe on 2018/03/20.
 */
data class WeatherData(val summary: String,
                       val icon: String,
                       val data: List<Entry>)