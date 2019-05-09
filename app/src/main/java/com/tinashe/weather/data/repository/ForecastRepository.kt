package com.tinashe.weather.data.repository

import com.tinashe.weather.data.model.Forecast
import io.reactivex.Observable

interface ForecastRepository {

    fun getForecast(latLong: String): Observable<Forecast>

    fun getForecast(latLong: String, tag: String): Observable<Forecast>

    fun getDayForecast(latLongTime: String) : Observable<Forecast>
}