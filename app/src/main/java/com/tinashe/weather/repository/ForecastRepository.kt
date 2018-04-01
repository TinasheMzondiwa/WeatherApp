package com.tinashe.weather.repository

import com.tinashe.weather.model.Forecast
import io.reactivex.Observable

interface ForecastRepository {

    fun getForecast(latLong: String): Observable<Forecast>
}