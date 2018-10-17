package com.tinashe.weather.ui.home.place

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.maps.model.LatLng
import com.tinashe.weather.db.dao.PlacesDao
import com.tinashe.weather.model.Forecast
import com.tinashe.weather.model.SavedPlace
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.model.ViewStateData
import com.tinashe.weather.repository.ForecastRepository
import com.tinashe.weather.ui.base.RxAwareViewModel
import com.tinashe.weather.ui.base.SingleLiveEvent
import com.tinashe.weather.utils.RxSchedulers
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

class PlaceForecastViewModel @Inject constructor(private val rxSchedulers: RxSchedulers,
                                                 private val forecastRepository: ForecastRepository,
                                                 private val placesDao: PlacesDao) : RxAwareViewModel() {

    private lateinit var geoDataClient: GeoDataClient
    private lateinit var placeId: String

    var placeHolder = MutableLiveData<SavedPlace>()
    var forecast = MutableLiveData<Forecast>()
    var viewState = SingleLiveEvent<ViewStateData>()
    var isBookmarked = MutableLiveData<Boolean>()

    override fun subscribe() {

    }

    fun initPlace(placeId: String, geoDataClient: GeoDataClient) {
        this.placeId = placeId
        this.geoDataClient = geoDataClient

        geoDataClient.getPlaceById(placeId).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val places = task.result ?: return@addOnCompleteListener
                val place = SavedPlace(places.get(0))
                placeHolder.value = place
                subscribeToBookmark()
                fetchForecast(place.latLng!!)
                Timber.i("Place found: %s", place.name)
                places.release()
            } else {
                Timber.e("Place not found.")
                viewState.value = ViewStateData(ViewState.ERROR, "Could not find details about this place!")
            }
        }
    }

    private fun subscribeToBookmark() {
        val disposable = placesDao.findPlace(placeId)
                .subscribeOn(rxSchedulers.database)
                .observeOn(rxSchedulers.main)
                .subscribe({
                    isBookmarked.value = true
                }, {
                    Timber.e(it)
                })

        disposables.add(disposable)
    }

    fun bookmarkClicked() {
        val place = placeHolder.value ?: return

        val disposable = if (isBookmarked.value == true) {
            Completable.fromAction { placesDao.delete(place) }
                    .subscribeOn(rxSchedulers.database)
                    .observeOn(rxSchedulers.main)
                    .subscribe({
                        isBookmarked.value = false
                        viewState.value = ViewStateData(ViewState.SUCCESS, "Removed from saved places")
                    }, { Timber.e(it) })
        } else {
            Completable.fromAction { placesDao.insert(place) }
                    .subscribeOn(rxSchedulers.database)
                    .observeOn(rxSchedulers.main)
                    .subscribe({
                        isBookmarked.value = true
                        viewState.value = ViewStateData(ViewState.SUCCESS, "Added to saved places")
                    }, { Timber.e(it) })
        }

        disposables.add(disposable)
    }

    private fun fetchForecast(latLng: LatLng) {
        val disposable = forecastRepository.getForecast("${latLng.latitude},${latLng.longitude}")
                .subscribeOn(rxSchedulers.network)
                .observeOn(rxSchedulers.main)
                .doOnSubscribe { viewState.value = ViewStateData(ViewState.LOADING) }
                .subscribe({ it ->
                    viewState.value = ViewStateData(ViewState.SUCCESS)

                    it.currently.location = placeHolder.value?.name ?: ""
                    it.currently.timeZone = it.timezone
                    val today = it.daily.data.first()
                    it.currently.sunriseTime = today.sunriseTime
                    it.currently.sunsetTime = today.sunsetTime
                    val timeZone = it.timezone
                    it.hourly.data.map { it.timeZone = timeZone }
                    it.daily.data.map { it.timeZone = timeZone }

                    forecast.value = it

                }, { throwable ->
                    Timber.e(throwable)
                    Crashlytics.logException(throwable)

                    throwable.message?.let {
                        viewState.value = ViewStateData(ViewState.ERROR, it)
                    }
                })
        disposables.add(disposable)
    }
}