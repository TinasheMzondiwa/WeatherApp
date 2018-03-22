package com.tinashe.weather

import android.app.Activity
import android.app.Application
import com.tinashe.weather.injection.DaggerWeatherAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tinashe on 2018/03/20.
 */
class WeatherApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

    object AppInjector {

        fun init(app: WeatherApp) {

            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }

            DaggerWeatherAppComponent.builder()
                    .application(app)
                    .build()
                    .inject(app)
        }
    }
}