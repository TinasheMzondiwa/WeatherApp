package com.tinashe.weather.utils

import com.tinashe.weather.data.model.DateFormat
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by tinashe on 2018/03/20.
 */
object DateUtil {

    fun getFormattedDate(date: Date, format: DateFormat): String {

        val simpleDateFormat = SimpleDateFormat(format.value, Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    fun getFormattedDate(time: Long, format: DateFormat, timeZone: String): String {

        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.of(timeZone))
        return localDateTime.format(DateTimeFormatter.ofPattern(format.value))
    }

}