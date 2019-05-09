package com.tinashe.weather.data.model

import androidx.annotation.StringRes

data class ViewStateData(
        val state: ViewState,

        val message: String? = null,

        @StringRes
        val messageId: Int? = null)
