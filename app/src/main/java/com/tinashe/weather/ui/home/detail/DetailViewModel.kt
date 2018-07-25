package com.tinashe.weather.ui.home.detail

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.tinashe.weather.model.*
import com.tinashe.weather.repository.ForecastRepository
import com.tinashe.weather.ui.base.RxAwareViewModel
import com.tinashe.weather.ui.base.SingleLiveEvent
import com.tinashe.weather.utils.DateUtil
import com.tinashe.weather.utils.RxSchedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DetailViewModel @Inject constructor(private val rxSchedulers: RxSchedulers,
                                          private val forecastRepository: ForecastRepository) : RxAwareViewModel() {

    var hourlyData = MutableLiveData<WeatherData>()
    var viewState = SingleLiveEvent<ViewStateData>()

    override fun subscribe() {

    }

    fun getForeCast(entry: Entry, location: Location) {

        val date = Date(TimeUnit.MILLISECONDS.convert(entry.time, TimeUnit.SECONDS))

        Answers.getInstance().logCustom(CustomEvent(EventName.GET_FORECAST_DETAIL)
                .putCustomAttribute("day", DateUtil.getFormattedDate(date, DateFormat.DAY)))

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