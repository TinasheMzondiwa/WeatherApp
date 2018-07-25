package com.tinashe.weather.utils.prefs

interface AppPrefs {

    fun getLastPromoShown(): Long

    fun setLastPromoShown(time: Long)
}