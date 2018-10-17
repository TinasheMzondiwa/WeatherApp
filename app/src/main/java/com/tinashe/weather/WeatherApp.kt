package com.tinashe.weather

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tinashe.weather.injection.DaggerWeatherAppComponent
import com.tinashe.weather.utils.CrashlyticsTree
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import io.fabric.sdk.android.Fabric
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

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

    object AppInjector {

        fun init(app: WeatherApp) {

            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            } else {
                Timber.plant(CrashlyticsTree())
            }

            Fabric.with(app, Crashlytics(), Answers())

            DaggerWeatherAppComponent.builder()
                    .application(app)
                    .build()
                    .inject(app)

            AndroidThreeTen.init(app)
        }
    }
}