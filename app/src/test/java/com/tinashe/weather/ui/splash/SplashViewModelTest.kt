package com.tinashe.weather.ui.splash

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.tinashe.weather.db.dao.LocationDao
import com.tinashe.weather.mock
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.utils.RxSchedulers
import com.tinashe.weather.whenever
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.verify

class SplashViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mockLocationDao = mock<LocationDao>()
    private val mockSchedulers = mock<RxSchedulers>()

    private val viewModel by lazy { SplashViewModel(mockLocationDao, mockSchedulers) }

    @Before
    fun initTest() {
        whenever(mockLocationDao.getCurrentLocation())
                .thenReturn(Maybe.empty())

        whenever(mockSchedulers.database)
                .thenReturn(Schedulers.trampoline())
        whenever(mockSchedulers.main)
                .thenReturn(Schedulers.trampoline())
    }

    @Test
    fun testInitState() {
        assertEquals(ViewState.LOADING, viewModel.viewState.value?.state)
    }

    @Test
    fun testPermNotGranted() {
        viewModel.permissionsNotGranted()
        assertEquals(ViewState.ERROR, viewModel.viewState.value?.state)
    }

    @Test
    fun testLocationReceived() {
        val mockLocation = mock<Location>()
        whenever(mockLocation.latitude)
                .thenReturn(-33.8342521)
        whenever(mockLocation.longitude)
                .thenReturn(18.5494933)

        viewModel.locationReceived(mockLocation, "Cape Town")

        verify(mockLocationDao).insert(any())
        assertEquals(ViewState.SUCCESS, viewModel.viewState.value?.state)
    }
}