package com.tinashe.weather.ui.home

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.tinashe.weather.db.dao.LocationDao
import com.tinashe.weather.db.dao.PlacesDao
import com.tinashe.weather.model.*
import com.tinashe.weather.model.event.WeatherEvent
import com.tinashe.weather.repository.ForecastRepository
import com.tinashe.weather.ui.base.RxAwareViewModel
import com.tinashe.weather.ui.base.SingleLiveEvent
import com.tinashe.weather.utils.RxBus
import com.tinashe.weather.utils.RxSchedulers
import com.tinashe.weather.utils.prefs.AppPrefs
import io.reactivex.Observable
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by tinashe on 2018/03/20.
 */
class HomeViewModel @Inject constructor(private val rxSchedulers: RxSchedulers,
                                        private val forecastRepository: ForecastRepository,
                                        private val locationDao: LocationDao,
                                        private val placesDao: PlacesDao,
                                        private val prefs: AppPrefs) : RxAwareViewModel() {

    var viewState = SingleLiveEvent<ViewStateData>()
    private var currentLocation = SingleLiveEvent<CurrentLocation>()
    var latestForecast = MutableLiveData<Forecast>()
    var promotePremium = SingleLiveEvent<Boolean>()
    var savedPlaces = MutableLiveData<ArrayList<SavedPlace>>()

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

        subscribeToSavedPlaces()
    }

    fun refreshForecast() {

        currentLocation.value?.let {

            if (!prefs.hasPremium() && latestForecast.value != null) {
                latestForecast.value?.let { forecast ->
                    viewState.value = ViewStateData(ViewState.SUCCESS)
                    forecast.currently.location = currentLocation.value?.name ?: ""

                    latestForecast.value = forecast
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

    private fun subscribeToSavedPlaces() {
        val disposable = placesDao.listAll()
                .subscribeOn(rxSchedulers.database)
                .observeOn(rxSchedulers.main)
                .subscribe({
                    val places = ArrayList(it)
                    savedPlaces.value = places
                    fetchWeather(places)
                }, { Timber.e(it) })

        disposables.add(disposable)
    }

    private fun fetchWeather(places: ArrayList<SavedPlace>) {
        val disposable = Observable.fromIterable(places)
                .flatMap {
                    val latLng = "${it.latLng?.latitude},${it.latLng?.longitude}"
                    forecastRepository.getForecast(latLng, it.placeId)
                }
                .subscribeOn(rxSchedulers.network)
                .observeOn(rxSchedulers.main)
                .subscribe({
                    RxBus.getInstance().send(WeatherEvent(it.tag!!, it.currently))

                }, { Timber.e(it) })

        disposables.add(disposable)
    }
}