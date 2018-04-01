package com.tinashe.weather.ui.home

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.location.Location
import com.nhaarman.mockito_kotlin.verify
import com.tinashe.weather.db.dao.LocationDao
import com.tinashe.weather.mock
import com.tinashe.weather.model.CurrentLocation
import com.tinashe.weather.model.Forecast
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.repository.ForecastRepository
import com.tinashe.weather.utils.RxSchedulers
import com.tinashe.weather.whenever
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
    private val mockSchedulers = mock<RxSchedulers>()
    private val mockRepository = mock<ForecastRepository>()
    private val mockLocation = mock<Location>()
    private val mockForecast = mock<Forecast>()

    private val viewModel by lazy { HomeViewModel(mockSchedulers, mockRepository, mockLocationDao) }

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

        whenever(mockSchedulers.database)
                .thenReturn(Schedulers.trampoline())
        whenever(mockSchedulers.network)
                .thenReturn(Schedulers.trampoline())
        whenever(mockSchedulers.main)
                .thenReturn(Schedulers.trampoline())


        whenever(mockRepository.getForecast(latLong))
                .thenReturn(Observable.just(mockForecast))
    }

    @Test
    fun testInitState() {
        assertEquals(ViewState.LOADING, viewModel.viewState.value?.state)
    }

    @Test
    fun testSubscribe_normal() {
        viewModel.subscribe()

        verify(mockRepository).getForecast(latLong)
        assertEquals(ViewState.SUCCESS, viewModel.viewState.value?.state)
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
        viewModel.subscribe()

        verify(mockRepository).getForecast(latLong)
        assertEquals(ViewState.ERROR, viewModel.viewState.value?.state)
        assertEquals(errorMsg, viewModel.viewState.value?.errorMessage)
    }

}