package com.tinashe.weather.ui.home.detail

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import com.tinashe.weather.model.Entry
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.model.ViewStateData
import com.tinashe.weather.model.WeatherData
import com.tinashe.weather.repository.ForecastRepository
import com.tinashe.weather.ui.base.RxAwareViewModel
import com.tinashe.weather.ui.base.SingleLiveEvent
import com.tinashe.weather.utils.RxSchedulers
import timber.log.Timber
import javax.inject.Inject

class DetailViewModel @Inject constructor(private val rxSchedulers: RxSchedulers,
                                          private val forecastRepository: ForecastRepository) : RxAwareViewModel() {

    var hourlyData = MutableLiveData<WeatherData>()
    var viewState = SingleLiveEvent<ViewStateData>()

    override fun subscribe() {

    }

    fun getForeCast(entry: Entry, location: Location) {

        val disposable = forecastRepository.getDayForecast("${location.latitude},${location.longitude},${entry.time}")
                .subscribeOn(rxSchedulers.network)
                .observeOn(rxSchedulers.main)
                .subscribe({
                    hourlyData.value = it.hourly
                    viewState.value = ViewStateData(ViewState.SUCCESS)
                }, {
                    Timber.e(it, it.message)
                    it.message?.let {
                        viewState.value = ViewStateData(ViewState.ERROR, it)
                    }
                })

        disposables.add(disposable)
    }
}