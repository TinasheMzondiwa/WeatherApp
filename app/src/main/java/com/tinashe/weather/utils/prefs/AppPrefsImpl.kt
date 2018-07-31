package com.tinashe.weather.utils.prefs

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class AppPrefsImpl constructor(context: Context) : AppPrefs {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getLastPromoShown(): Long {
        return prefs.getLong(KEY_PROMO_LAST_SHOWN, 0)
    }

    override fun setLastPromoShown(time: Long) {
        prefs.edit()
                .putLong(KEY_PROMO_LAST_SHOWN, time)
                .apply()
    }

    override fun hasPremium(): Boolean = prefs.getBoolean(KEY_HAS_PREMIUM, false)

    override fun setHasPremium() {
        prefs.edit()
                .putBoolean(KEY_HAS_PREMIUM, true)
                .apply()
    }

    companion object {
        private const val KEY_PROMO_LAST_SHOWN = "KEY_PROMO_LAST_SHOWN"
        private const val KEY_HAS_PREMIUM = "KEY_HAS_PREMIUM"
    }
}