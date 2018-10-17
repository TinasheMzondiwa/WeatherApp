package com.tinashe.weather.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tinashe.weather.mock
import com.tinashe.weather.model.DateFormat
import com.tinashe.weather.whenever
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import java.util.*

class WeatherUtilsTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val mockContext = mock<Context>()

    @Test
    fun testDateUtil() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2018)
        calendar.set(Calendar.MONTH, Calendar.APRIL)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 13)
        calendar.set(Calendar.MINUTE, 4)

        var formatted = DateUtil.getFormattedDate(calendar.time, DateFormat.DAY)
        assertEquals("Sunday, 01 Apr", formatted)

        formatted = DateUtil.getFormattedDate(calendar.time, DateFormat.TIME)
        assertEquals("01:04 PM", formatted)

        formatted = DateUtil.getFormattedDate(calendar.time, DateFormat.TIME_SHORT)
        assertEquals("13:04", formatted)
    }

    @Test
    fun testHasConnection() {

        val mockInfo = mock<NetworkInfo>()
        whenever(mockInfo.isConnected)
                .thenReturn(true)
        val mockManager = mock<ConnectivityManager>()
        whenever(mockManager.activeNetworkInfo)
                .thenReturn(mockInfo)

        whenever(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockManager)

        assertEquals(true, WeatherUtil.hasConnection(mockContext))
    }

    @Test
    fun testGetWeatherIcon() {
        val mockDrawable = mock<Drawable>()
        val mockResources = mock<Resources>()

        whenever(mockResources.getDrawable(anyInt()))
                .thenReturn(mockDrawable)

        whenever(mockContext.resources)
                .thenReturn(mockResources)

        val icon = WeatherUtil.getIcon(mockContext, "cloudy")
        assertNotNull(icon)
    }
}