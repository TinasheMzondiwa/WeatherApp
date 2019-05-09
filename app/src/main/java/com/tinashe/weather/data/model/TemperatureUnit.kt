package com.tinashe.weather.data.model

enum class TemperatureUnit(val value: String) {

    CELSIUS("celsius"),

    FAHRENHEIT("fahrenheit");

    companion object {
        private val map = values().associateBy(TemperatureUnit::value)

        fun fromValue(value: String) = map[value]
    }
}
