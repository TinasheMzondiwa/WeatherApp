package com.tinashe.weather.ui.home

import android.arch.lifecycle.MutableLiveData
import android.location.Location
import com.crashlytics.android.Crashlytics
import com.tinashe.weather.db.dao.LocationDao
import com.tinashe.weather.model.CurrentLocation
import com.tinashe.weather.model.Forecast
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.model.ViewStateData
import com.tinashe.weather.repository.ForecastRepository
import com.tinashe.weather.ui.base.RxAwareViewModel
import com.tinashe.weather.ui.base.SingleLiveEvent
import com.tinashe.weather.utils.RxSchedulers
import com.tinashe.weather.utils.prefs.AppPrefs
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by tinashe on 2018/03/20.
 */
class HomeViewModel @Inject constructor(private val rxSchedulers: RxSchedulers,
                                        private val forecastRepository: ForecastRepository,
                                        private val locationDao: LocationDao,
                                        private val prefs: AppPrefs) : RxAwareViewModel() {

    var viewState: SingleLiveEvent<ViewStateData> = SingleLiveEvent()
    private var currentLocation: SingleLiveEvent<CurrentLocation> = SingleLiveEvent()
    var latestForecast: MutableLiveData<Forecast> = MutableLiveData()
    var promotePremium = SingleLiveEvent<Boolean>()

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

    }

    fun subscribe(location: Location, area: String) {
        currentLocation.value = CurrentLocation(area, "${location.latitude},${location.longitude}")
        refreshForecast()
    }

    fun refreshForecast() {

        currentLocation.value?.let {

            if (!prefs.hasPremium() && latestForecast.value != null) {
                latestForecast.value?.let {
                    viewState.value = ViewStateData(ViewState.SUCCESS)
                    it.currently.location = currentLocation.value?.name ?: ""

                    latestForecast.value = it
                }

                promotePremium.value = showPromo()

                return
            }

            val disposable = forecastRepository.getForecast(it.latLong)
                    .subscribeOn(rxSchedulers.network)
                    .observeOn(rxSchedulers.main)
                    .doOnSubscribe { viewState.value = ViewStateData(ViewState.LOADING) }
                    .subscribe({
                        viewState.value = ViewStateData(ViewState.SUCCESS)
                        it.currently.location = currentLocation.value?.name ?: ""
                        it.currently.timeZone = it.timezone
                        val today = it.daily.data.first()
                        it.currently.sunriseTime = today.sunriseTime
                        it.currently.sunsetTime = today.sunsetTime
                        val timeZone = it.timezone
                        it.hourly.data.map { it.timeZone = timeZone }
                        it.daily.data.map { it.timeZone = timeZone }

                        latestForecast.value = it

                    }, {
                        Timber.e(it, it.message)
                        try {
                            Crashlytics.logException(it)
                        } catch (ex: Exception) {
                            //Unit tests fail when Crashlytics is called
                        }

                        it.message?.let {
                            viewState.value = ViewStateData(ViewState.ERROR, it)
                        }
                    })
            disposables.add(disposable)
        } ?: readLocation()

    }

    private fun showPromo(): Boolean {
        val today = Calendar.getInstance()

        val time = prefs.getLastPromoShown()

        if (time <= 0) {
            prefs.setLastPromoShown(today.timeInMillis)
            return true
        }

        val last = Calendar.getInstance()
        last.timeInMillis = time

        val diff = today.timeInMillis - last.timeInMillis
        val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

        return if (days > 1) {
            prefs.setLastPromoShown(today.timeInMillis)
            true
        } else false
    }

    fun premiumUnlocked() {
        if (!prefs.hasPremium()) {
            prefs.setHasPremium()

            refreshForecast()
        }
    }

    fun hasPremium(): Boolean = prefs.hasPremium()
}