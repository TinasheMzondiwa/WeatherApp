package com.tinashe.weather

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.api.Places
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tinashe.weather.data.di.DaggerWeatherAppComponent
import com.tinashe.weather.utils.WeatherUtil
import com.tinashe.weather.utils.prefs.AppPrefs
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tinashe on 2018/03/20.
 */
class WeatherApp : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var prefs: AppPrefs

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        DaggerWeatherAppComponent.builder()
                .application(this)
                .build()
                .inject(this)

        AndroidThreeTen.init(this)

        WeatherUtil.applyTheme(prefs.getThemeStyle())

        Places.initialize(applicationContext, BuildConfig.PLACES_API_KEY)
    }
}