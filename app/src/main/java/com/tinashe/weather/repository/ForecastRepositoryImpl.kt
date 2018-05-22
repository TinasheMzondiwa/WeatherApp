package com.tinashe.weather.repository

import android.content.Context
import com.tinashe.weather.BuildConfig
import com.tinashe.weather.R
import com.tinashe.weather.model.Forecast
import com.tinashe.weather.retrofit.WeatherApi
import com.tinashe.weather.utils.WeatherUtil
import io.reactivex.Observable

class ForecastRepositoryImpl constructor(private val context: Context,
                                         private val weatherApi: WeatherApi) : ForecastRepository {

    override fun getForecast(latLong: String): Observable<Forecast> {
        return if (WeatherUtil.hasConnection(context)) {
            weatherApi.getForecast(BuildConfig.API_SECRET, latLong)
                    .flatMap { response ->
                        if (response.isSuccessful) {
                            Observable.just(response.body())
                        } else {
                            Observable.error(Exception(context.getString(R.string.default_error)))
                        }
                    }
        } else {
            Observable.error(Exception(context.getString(R.string.connection_error)))
        }
    }

    override fun getDayForecast(latLongTime: String): Observable<Forecast> {
        return if(WeatherUtil.hasConnection(context)){
            weatherApi.getTimeForecast(BuildConfig.API_SECRET, latLongTime)
                    .flatMap { response ->
                        if (response.isSuccessful) {
                            Observable.just(response.body())
                        } else {
                            Observable.error(Exception(context.getString(R.string.default_error)))
                        }
                    }
        } else {
            Observable.error(Exception(context.getString(R.string.connection_error)))
        }
    }
}