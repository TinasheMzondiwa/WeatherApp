package com.tinashe.weather.ui.home.place

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.libraries.places.api.net.PlacesClient
import com.tinashe.weather.data.db.dao.PlacesDao
import com.tinashe.weather.data.model.Forecast
import com.tinashe.weather.data.repository.ForecastRepository
import com.tinashe.weather.extensions.RxSchedulers
import com.tinashe.weather.mock
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
    private val mockPlacesClient = mock<PlacesClient>()
    private val mockPlacesDao = mock<PlacesDao>()

    private val viewModel by lazy { PlaceForecastViewModel(mockSchedulers, mockRepository, mockPlacesDao) }

    private val placeId: String = "place_id"

    @Before
    fun initTest() {
        //TODO: Mock placesClient implementation
    }

    @Ignore
    @Test
    fun testInit() {
        viewModel.initPlace(placeId, mockPlacesClient)

        assertNotNull(viewModel.placeHolder.value)
    }
}