package com.tinashe.weather.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import com.tinashe.weather.BuildConfig
import com.tinashe.weather.R
import com.tinashe.weather.model.Entry
import com.tinashe.weather.ui.splash.SplashActivity
import java.util.*

object ShortcutsHelper {

    private const val SHORT_CUT_ID = "${BuildConfig.APPLICATION_ID}_short_cut_id"

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun update(context: Context, entry: Entry) {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)

        val shortcut = ShortcutInfo.Builder(context, SHORT_CUT_ID)
                .setDisabledMessage(context.getString(R.string.app_name))
                .setShortLabel(entry.summary)
                .setLongLabel(context.getString(R.string.degrees, entry.temperature.toInt()))
                .setIcon(Icon.createWithResource(context, WeatherUtil.getIconRes(entry.icon)))
                .setIntents(arrayOf(Intent(Intent.ACTION_MAIN, Uri.EMPTY, context,
                        SplashActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)))
                .build()

        shortcutManager.dynamicShortcuts = Arrays.asList(shortcut)
    }
}