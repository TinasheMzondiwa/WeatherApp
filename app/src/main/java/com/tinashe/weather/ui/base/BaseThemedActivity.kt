package com.tinashe.weather.ui.base

import android.annotation.TargetApi
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.View


/**
 * Created by tinashe on 2018/03/22.
 */
abstract class BaseThemedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                disableLightStatusBar()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun disableLightStatusBar() {
        val view = window.decorView
        var flags = view.systemUiVisibility
        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        view.systemUiVisibility = flags
    }
}