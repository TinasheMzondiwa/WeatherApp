package com.tinashe.weather.ui.home.detail

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.tinashe.weather.data.model.Entry
import com.tinashe.weather.data.model.ViewState
import com.tinashe.weather.data.model.ViewStateData
import com.tinashe.weather.data.model.WeatherData
import com.tinashe.weather.data.repository.ForecastRepository
import com.tinashe.weather.extensions.RxSchedulers
import com.tinashe.weather.ui.base.BaseViewModel
import com.tinashe.weather.ui.base.SingleLiveEvent
import timber.log.Timber
import javax.inject.Inject

class DetailViewModel @Inject constructor(private val rxSchedulers: RxSchedulers,
                                          private val forecastRepository: ForecastRepository) : BaseViewModel() {

    var hourlyData = MutableLiveData<WeatherData>()
    var viewState = SingleLiveEvent<ViewStateData>()

    override fun subscribe() {

    }

    fun getForeCast(entry: Entry, location: Location) {

        val disposable = forecastRepository.getDayForecast("${location.latitude},${location.longitude},${entry.time}")
                .subscribeOn(rxSchedulers.network)
                .observeOn(rxSchedulers.main)
                .subscribe({ forecast ->

                    val timeZone = forecast.timezone
                    forecast.hourly.data.forEach { it.timeZone = timeZone }
                    hourlyData.value = forecast.hourly
                    viewState.value = ViewStateData(ViewState.SUCCESS)

                }, { throwable ->
                    Timber.e(throwable)

                    throwable.message?.let {
                        viewState.value = ViewStateData(ViewState.ERROR, it)
                    }
                })

        disposables.add(disposable)
    }
}