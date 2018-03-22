package com.tinashe.weather.model

import android.support.annotation.StringRes

class ViewStateData {

    @ViewState
    @get:ViewState
    val state: Int

    var errorMessage: String? = null
        private set

    @StringRes
    var errorRes: Int? = null

    constructor(@ViewState state: Int) {
        this.state = state
    }

    constructor(@ViewState state: Int, errorMessage: String) {
        this.state = state
        this.errorMessage = errorMessage
    }

    constructor(@ViewState state: Int, @StringRes errorRes: Int) {
        this.state = state
        this.errorRes = errorRes
    }
}