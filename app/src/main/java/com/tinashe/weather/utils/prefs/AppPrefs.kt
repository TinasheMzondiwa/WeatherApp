package com.tinashe.weather.utils.prefs

interface AppPrefs {

    fun getLastPromoShown(): Long

    fun setLastPromoShown(time: Long)

    fun hasPremium(): Boolean

    fun setHasPremium()
}