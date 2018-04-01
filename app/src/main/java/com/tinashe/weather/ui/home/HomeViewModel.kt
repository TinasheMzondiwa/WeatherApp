package com.tinashe.weather.ui.home

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import com.tinashe.weather.db.dao.LocationDao
import com.tinashe.weather.model.CurrentLocation
import com.tinashe.weather.model.Forecast
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.model.ViewStateData
import com.tinashe.weather.repository.ForecastRepository
import com.tinashe.weather.ui.base.RxAwareViewModel
import com.tinashe.weather.ui.base.SingleLiveEvent
import com.tinashe.weather.utils.RxSchedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tinashe on 2018/03/20.
 */
class HomeViewModel @Inject constructor(private val rxSchedulers: RxSchedulers,
                                        private val forecastRepository: ForecastRepository,
                                        private val locationDao: LocationDao) : RxAwareViewModel() {

    var viewState: SingleLiveEvent<ViewStateData> = SingleLiveEvent()
    private var currentLocation: SingleLiveEvent<CurrentLocation> = SingleLiveEvent()
    var latestForecast: MutableLiveData<Forecast> = MutableLiveData()

    init {
        viewState.value = ViewStateData(ViewState.LOADING)
    }

    private fun readLocation() {
        val disposable = locationDao.getCurrentLocation()
                .subscribeOn(rxSchedulers.database)
                .observeOn(rxSchedulers.main)
                .subscribe({
                    currentLocation.value = it
                    refreshForecast()
                }, { Timber.e(it, it.message) })

        disposables.add(disposable)
    }

    override fun subscribe() {
        refreshForecast()
    }

    fun subscribe(location: Location, area: String) {
        currentLocation.value = CurrentLocation(area, "${location.latitude},${location.longitude}")
        refreshForecast()
    }

    fun refreshForecast() {
        currentLocation.value?.let {
            val disposable = forecastRepository.getForecast(it.latLong)
                    .subscribeOn(rxSchedulers.network)
                    .observeOn(rxSchedulers.main)
                    .doOnSubscribe { viewState.value = ViewStateData(ViewState.LOADING) }
                    .subscribe({
                        viewState.value = ViewStateData(ViewState.SUCCESS)
                        it.currently.location = currentLocation.value?.name ?: ""

                        latestForecast.value = it

                    }, {
                        Timber.e(it, it.message)

                        it.message?.let {
                            viewState.value = ViewStateData(ViewState.ERROR, it)
                        }
                    })
            disposables.add(disposable)
        } ?: readLocation()

    }
}