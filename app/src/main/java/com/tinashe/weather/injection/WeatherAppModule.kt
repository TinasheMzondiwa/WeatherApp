package com.tinashe.weather.injection

import android.content.Context
import com.tinashe.weather.WeatherApp
import com.tinashe.weather.db.WeatherAppDb
import com.tinashe.weather.db.dao.LocationDao
import com.tinashe.weather.db.dao.PlacesDao
import com.tinashe.weather.repository.ForecastRepository
import com.tinashe.weather.repository.ForecastRepositoryImpl
import com.tinashe.weather.retrofit.RestClient
import com.tinashe.weather.retrofit.WeatherApi
import com.tinashe.weather.utils.RxSchedulers
import com.tinashe.weather.utils.prefs.AppPrefs
import com.tinashe.weather.utils.prefs.AppPrefsImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by tinashe on 2018/03/20.
 */
@Module
internal class WeatherAppModule {

    @Provides
    @Singleton
    fun provideContext(app: WeatherApp): Context = app

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi = RestClient.createService(WeatherApi::class.java)

    @Provides
    @Singleton
    fun provideRxSchedulers(): RxSchedulers = RxSchedulers()

    @Provides
    @Singleton
    fun provideDatabase(context: Context): WeatherAppDb = WeatherAppDb.create(context)

    @Provides
    @Singleton
    fun provideLocationDao(database: WeatherAppDb): LocationDao = database.locationDao()

    @Provides
    @Singleton
    fun providePlacesDao(database: WeatherAppDb): PlacesDao = database.placesDao()

    @Provides
    @Singleton
    fun providePrefs(context: Context): AppPrefs = AppPrefsImpl(context)

    @Provides
    @Singleton
    fun provideForecastRepository(context: Context, weatherApi: WeatherApi): ForecastRepository =
            ForecastRepositoryImpl(context, weatherApi)
}