package com.tinashe.weather.retrofit

import com.tinashe.weather.model.Forecast
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by tinashe on 2018/03/20.
 */
interface WeatherApi {

    @GET(value = "forecast/{key}/{latLong}?exclude=alerts,flags,minutely&units=si")
    fun getForecast(@Path("key") key: String, @Path("latLong") latLong: String): Observable<Response<Forecast>>
}