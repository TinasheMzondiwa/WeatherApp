package com.tinashe.weather.ui.home

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.verify
import com.tinashe.weather.data.db.dao.LocationDao
import com.tinashe.weather.data.db.dao.PlacesDao
import com.tinashe.weather.data.model.CurrentLocation
import com.tinashe.weather.data.model.Forecast
import com.tinashe.weather.data.model.ViewState
import com.tinashe.weather.data.repository.ForecastRepository
import com.tinashe.weather.extensions.RxSchedulers
import com.tinashe.weather.mock
import com.tinashe.weather.utils.prefs.AppPrefs
import com.tinashe.weather.whenever
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mockLocationDao = mock<LocationDao>()
    private val mockPlacesDao = mock<PlacesDao>()
    private val mockSchedulers = mock<RxSchedulers>()
    private val mockRepository = mock<ForecastRepository>()
    private val mockLocation = mock<Location>()
    private val mockForecast = mock<Forecast>()
    private val mockPrefs = mock<AppPrefs>()

    private val viewModel by lazy { HomeViewModel(mockSchedulers, mockRepository, mockLocationDao, mockPlacesDao, mockPrefs) }

    private val latLong by lazy { "${mockLocation.latitude},${mockLocation.longitude}" }

    @Before
    fun initTest() {
        whenever(mockLocation.latitude)
                .thenReturn(-33.8342521)
        whenever(mockLocation.longitude)
                .thenReturn(18.5494933)

        val capeTown = CurrentLocation("Cape Town", latLong)
        whenever(mockLocationDao.getCurrentLocation())
                .thenReturn(Maybe.just(capeTown))

        whenever(mockPlacesDao.listAll()).thenReturn(Flowable.just(emptyList()))

        whenever(mockSchedulers.database)
                .thenReturn(Schedulers.trampoline())
        whenever(mockSchedulers.network)
                .thenReturn(Schedulers.trampoline())
        whenever(mockSchedulers.main)
                .thenReturn(Schedulers.trampoline())


        whenever(mockRepository.getForecast(latLong))
                .thenReturn(Observable.just(mockForecast))

        whenever(mockPrefs.hasPremium()).thenReturn(true)
    }

    @Test
    fun testInitState() {
        assertEquals(ViewState.LOADING, viewModel.viewState.value?.state)
    }

    @Test
    fun testSubscribe_withLocation() {
        viewModel.subscribe(mockLocation, "Cape Town")

        verify(mockRepository).getForecast(latLong)
        assertEquals(ViewState.SUCCESS, viewModel.viewState.value?.state)
    }

    @Test
    fun testSubscribe_errorState() {
        val errorMsg = "Error fetching forecast"

        whenever(mockRepository.getForecast(latLong))
                .thenReturn(Observable.error(Exception(errorMsg)))

        viewModel.subscribe(mockLocation, "Cape Town")

        verify(mockRepository).getForecast(latLong)
        assertEquals(ViewState.ERROR, viewModel.viewState.value?.state)
        assertEquals(errorMsg, viewModel.viewState.value?.message)
    }

}