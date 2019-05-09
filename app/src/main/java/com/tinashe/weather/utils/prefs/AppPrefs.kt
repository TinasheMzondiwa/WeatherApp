package com.tinashe.weather.utils.prefs

import com.tinashe.weather.data.model.TemperatureUnit

interface AppPrefs {

    fun getLastPromoShown(): Long

    fun setLastPromoShown(time: Long)

    fun hasPremium(): Boolean

    fun setHasPremium()

    fun getTemperatureUnit(): TemperatureUnit

    fun setTemperatureUnit(unit: TemperatureUnit)
}