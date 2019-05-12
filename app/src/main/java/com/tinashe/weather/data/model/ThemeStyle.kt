package com.tinashe.weather.data.model

enum class ThemeStyle(val value: String) {

    LIGHT_MODE("light"),
    DARK_MODE("dark"),
    DEFAULT_MODE("default");

    companion object {
        private val map = values().associateBy(ThemeStyle::value)

        fun fromValue(value: String) = map[value]
    }
}