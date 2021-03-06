package com.tinashe.weather.ui.splash

import android.location.Location
import com.tinashe.weather.data.db.dao.LocationDao
import com.tinashe.weather.data.model.CurrentLocation
import com.tinashe.weather.data.model.ViewState
import com.tinashe.weather.data.model.ViewStateData
import com.tinashe.weather.extensions.RxSchedulers
import com.tinashe.weather.ui.base.BaseViewModel
import com.tinashe.weather.ui.base.SingleLiveEvent
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tinashe on 2018/03/20.
 */
class SplashViewModel @Inject constructor(private val locationDao: LocationDao,
                                          private val rxSchedulers: RxSchedulers) : BaseViewModel() {

    var viewState = SingleLiveEvent<ViewStateData>()

    private var currentLocation = SingleLiveEvent<CurrentLocation>()

    init {
        viewState.value = ViewStateData(ViewState.LOADING)

        val dummyLocation = CurrentLocation("", "")
        dummyLocation.id = -1L

        val disposable = locationDao.getCurrentLocation()
                .defaultIfEmpty(dummyLocation)
                .subscribeOn(rxSchedulers.database)
                .observeOn(rxSchedulers.main)
                .subscribe({
                    currentLocation.value = it
                }, { Timber.e(it) })

        disposables.add(disposable)
    }

    override fun subscribe() {

    }

    fun permissionsNotGranted() {
        viewState.value = ViewStateData(ViewState.ERROR)
    }

    fun locationReceived(location: Location, area: String) {
        val latLong = "${location.latitude},${location.longitude}"

        val currentLoc = currentLocation.value

        currentLoc?.let {
            if (it.id == -1L) {
                addLocation(CurrentLocation(area, latLong))
            } else {
                it.latLong = latLong
                it.name = area

                val disposable = locationDao.update(it)
                        .subscribeOn(rxSchedulers.database)
                        .observeOn(rxSchedulers.main)
                        .subscribe({
                            viewState.value = ViewStateData(ViewState.SUCCESS)
                        }, { Timber.e(it) })

                disposables.add(disposable)
            }
        } ?: addLocation(CurrentLocation(area, latLong))
    }

    private fun addLocation(location: CurrentLocation) {
        val disposable = locationDao.insert(location)
                .subscribeOn(rxSchedulers.database)
                .observeOn(rxSchedulers.main)
                .subscribe({
                    viewState.value = ViewStateData(ViewState.SUCCESS)
                }, { Timber.e(it) })

        disposables.add(disposable)
    }
}