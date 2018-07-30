package com.tinashe.weather.injection

import com.tinashe.weather.ui.about.AppInfoActivity
import com.tinashe.weather.ui.home.HomeActivity
import com.tinashe.weather.ui.home.detail.DetailFragment
import com.tinashe.weather.ui.home.place.PlaceForecastActivity
import com.tinashe.weather.ui.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by tinashe on 2018/03/20.
 */
@Module
internal abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun bindDetailFragment(): DetailFragment

    @ContributesAndroidInjector
    abstract fun bindAppInfoActivity(): AppInfoActivity

    @ContributesAndroidInjector
    abstract fun bindPlaceForecastActivity(): PlaceForecastActivity
}