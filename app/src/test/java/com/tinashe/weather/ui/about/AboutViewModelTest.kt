package com.tinashe.weather.ui.about

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class AboutViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val viewModel by lazy { AboutInfoViewModel() }

    @Test
    fun testInitState() {

        assertNotNull(viewModel)
        assertNotNull(viewModel.infoItems)
        assertEquals(4, viewModel.infoItems.value?.size)
    }
}