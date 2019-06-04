package com.tinashe.weather.ui.home.place

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.tinashe.weather.data.db.dao.PlacesDao
import com.tinashe.weather.data.model.Forecast
import com.tinashe.weather.data.model.SavedPlace
import com.tinashe.weather.data.model.ViewState
import com.tinashe.weather.data.model.ViewStateData
import com.tinashe.weather.data.repository.ForecastRepository
import com.tinashe.weather.extensions.RxSchedulers
import com.tinashe.weather.ui.base.BaseViewModel
import com.tinashe.weather.ui.base.SingleLiveEvent
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class PlaceForecastViewModel @Inject constructor(private val rxSchedulers: RxSchedulers,
                                                 private val forecastRepository: ForecastRepository,
                                                 private val placesDao: PlacesDao) : BaseViewModel() {

    private lateinit var placesClient: PlacesClient
    private lateinit var placeId: String

    var placeHolder = MutableLiveData<SavedPlace>()
    var forecast = MutableLiveData<Forecast>()
    var viewState = SingleLiveEvent<ViewStateData>()
    var isBookmarked = MutableLiveData<Boolean>()

    override fun subscribe() {

    }

    fun initPlace(placeId: String, placesClient: PlacesClient) {
        this.placeId = placeId
        this.placesClient = placesClient

        val placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG)

        val request = FetchPlaceRequest.builder(placeId, placeFields)
                .build()
        placesClient.fetchPlace(request)
                .addOnSuccessListener {
                    val place = SavedPlace(it.place)

                    placeHolder.value = place
                    subscribeToBookmark()
                    fetchForecast(place.latLng)
                    Timber.i("Place found: %s", place.name)
                }
                .addOnFailureListener {
                    Timber.e("Place not found.")
                    viewState.value = ViewStateData(ViewState.ERROR, "Could not find details about this place!")
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
            placesDao.delete(place)
                    .subscribeOn(rxSchedulers.database)
                    .observeOn(rxSchedulers.main)
                    .subscribe({
                        isBookmarked.value = false
                        viewState.value = ViewStateData(ViewState.SUCCESS, "Removed from saved places")
                    }, { Timber.e(it) })
        } else {
            placesDao.insert(place)
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
                    it.hourly.data.forEach { it.timeZone = timeZone }
                    it.daily.data.forEach { it.timeZone = timeZone }

                    forecast.value = it

                }, { throwable ->
                    Timber.e(throwable)

                    throwable.message?.let {
                        viewState.value = ViewStateData(ViewState.ERROR, it)
                    }
                })

        disposables.add(disposable)
    }
}