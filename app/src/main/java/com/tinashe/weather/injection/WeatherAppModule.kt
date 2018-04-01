package com.tinashe.weather.injection

import android.arch.persistence.room.Room
import android.content.Context
import com.tinashe.weather.WeatherApp
import com.tinashe.weather.db.WeatherAppDb
import com.tinashe.weather.repository.ForecastRepository
import com.tinashe.weather.repository.ForecastRepositoryImpl
import com.tinashe.weather.retrofit.RestClient
import com.tinashe.weather.retrofit.WeatherApi
import com.tinashe.weather.utils.RxSchedulers
import dagger.Module
import dagger.Provides

/**
 * Created by tinashe on 2018/03/20.
 */
@Module
internal class WeatherAppModule {

    @Provides
    fun provideContext(app: WeatherApp): Context = app

    @Provides
    fun provideWeatherApi(): WeatherApi = RestClient.createService(WeatherApi::class.java)

    @Provides
    fun provideRxSchedulers(): RxSchedulers = RxSchedulers()

    @Provides
    fun provideDatabase(context: Context): WeatherAppDb = Room.databaseBuilder(context,
            WeatherAppDb::class.java, WeatherAppDb.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePersonDao(database: WeatherAppDb) = database.locationDao()

    @Provides
    fun provideForecastRepository(context: Context, weatherApi: WeatherApi): ForecastRepository =
            ForecastRepositoryImpl(context, weatherApi)
}