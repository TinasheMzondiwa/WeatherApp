package com.tinashe.weather.ui.home.place

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.location.places.GeoDataClient
import com.tinashe.weather.db.dao.PlacesDao
import com.tinashe.weather.mock
import com.tinashe.weather.model.Forecast
import com.tinashe.weather.repository.ForecastRepository
import com.tinashe.weather.utils.RxSchedulers
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class PlaceForecastViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mockSchedulers = mock<RxSchedulers>()
    private val mockRepository = mock<ForecastRepository>()
    private val mockForecast = mock<Forecast>()
    private val mockGeoDataClient = mock<GeoDataClient>()
    private val mockPlacesDao = mock<PlacesDao>()

    private val viewModel by lazy { PlaceForecastViewModel(mockSchedulers, mockRepository, mockPlacesDao) }

    private val placeId: String = "place_id"

    @Before
    fun initTest() {
        //TODO: Mock geoDataClient implementation
    }

    @Ignore
    @Test
    fun testInit() {
        viewModel.initPlace(placeId, mockGeoDataClient)

        assertNotNull(viewModel.placeHolder.value)
    }
}