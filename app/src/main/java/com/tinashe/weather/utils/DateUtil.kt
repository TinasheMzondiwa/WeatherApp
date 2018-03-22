package com.tinashe.weather.utils

import com.tinashe.weather.model.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tinashe on 2018/03/20.
 */
object DateUtil {

    fun getFormattedDate(date: Date, @DateFormat dateType: String): String {

        val simpleDateFormat = SimpleDateFormat(dateType, Locale.getDefault())
        return simpleDateFormat.format(date)
    }

}