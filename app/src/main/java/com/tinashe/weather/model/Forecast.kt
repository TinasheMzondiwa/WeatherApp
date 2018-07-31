package com.tinashe.weather.model

/**
 * Created by tinashe on 2018/03/20.
 */
data class Forecast(val timezone: String,
                    val currently: Entry,
                    val hourly: WeatherData,
                    val daily: WeatherData,
                    var tag: String? = null)