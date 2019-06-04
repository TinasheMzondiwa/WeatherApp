package com.tinashe.weather.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by tinashe on 2018/03/20.
 */
@Parcelize
data class Entry(val time: Long,
                 val summary: String,
                 val icon: String,
                 val precipType: String,
                 val temperature: Double,
                 var location: String = "",
                 val temperatureHigh: Double,
                 val temperatureMin: Double,
                 var timeZone: String = "",
                 var sunriseTime: Long,
                 var sunsetTime: Long) : Parcelable