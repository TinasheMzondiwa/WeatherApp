package com.tinashe.weather.utils.prefs

import com.tinashe.weather.model.TemperatureUnit

interface AppPrefs {

    fun getLastPromoShown(): Long

    fun setLastPromoShown(time: Long)

    fun hasPremium(): Boolean

    fun setHasPremium()

    @TemperatureUnit
    fun getTemperatureUnit(): String

    fun setTemperatureUnit(@TemperatureUnit unit: String)
}