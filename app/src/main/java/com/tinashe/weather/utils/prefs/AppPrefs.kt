package com.tinashe.weather.utils.prefs

import com.tinashe.weather.data.model.TemperatureUnit
import com.tinashe.weather.data.model.ThemeStyle

interface AppPrefs {

    fun getThemeStyle(): ThemeStyle

    fun setThemeStyle(style: ThemeStyle)

    fun getLastPromoShown(): Long

    fun setLastPromoShown(time: Long)

    fun hasPremium(): Boolean

    fun setHasPremium()

    fun getTemperatureUnit(): TemperatureUnit

    fun setTemperatureUnit(unit: TemperatureUnit)
}