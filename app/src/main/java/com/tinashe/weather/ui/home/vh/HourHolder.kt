package com.tinashe.weather.ui.home.vh

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.weather.R
import com.tinashe.weather.model.DateFormat
import com.tinashe.weather.model.Entry
import com.tinashe.weather.model.TemperatureUnit
import com.tinashe.weather.utils.DateUtil
import com.tinashe.weather.utils.WeatherUtil
import com.tinashe.weather.utils.inflateView
import com.tinashe.weather.utils.toFahrenheit
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.weather_hour_item.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * Created by tinashe on 2018/03/21.
 */
class HourHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(entry: Entry, @TemperatureUnit unit: String) {
        val context = itemView.context

        val time = LocalDateTime.ofInstant(Instant.ofEpochSecond(entry.time), ZoneId.of(entry.timeZone))
        val now = LocalDateTime.now(ZoneId.of(entry.timeZone))

        hourTime.text = if (time.isBefore(now.plusMinutes(5))) {
            context.getString(R.string.now)
        } else {
            DateUtil.getFormattedDate(entry.time, DateFormat.TIME_SHORT, entry.timeZone)
        }

        hourIcon.setImageDrawable(WeatherUtil.getIcon(context, entry.icon))

        val temp = when (unit) {
            TemperatureUnit.FAHRENHEIT -> entry.temperature.toFahrenheit()
            else -> entry.temperature
        }

        hourTemperature.text = context.getString(R.string.degrees, temp.toInt())
    }

    companion object {
        fun inflate(parent: ViewGroup):
                HourHolder = HourHolder(inflateView(R.layout.weather_hour_item, parent, false))
    }
}