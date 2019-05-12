package com.tinashe.weather.ui.home.vh

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.weather.R
import com.tinashe.weather.data.model.DateFormat
import com.tinashe.weather.data.model.Entry
import com.tinashe.weather.data.model.TemperatureUnit
import com.tinashe.weather.extensions.inflateView
import com.tinashe.weather.extensions.toFahrenheit
import com.tinashe.weather.utils.DateUtil
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.weather_data_day_item.*


/**
 * Created by tinashe on 2018/03/21.
 */
class DayHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(entry: Entry, unit: TemperatureUnit, onClick: ((Entry) -> Unit)? = null) {
        val context = itemView.context

        dayDate.text = DateUtil.getFormattedDate(entry.time, DateFormat.DAY, entry.timeZone)
        daySummary.text = entry.summary

        var min = entry.temperatureMin
        var max = entry.temperatureHigh

        if (unit == TemperatureUnit.FAHRENHEIT) {
            min = entry.temperatureMin.toFahrenheit()
            max = entry.temperatureHigh.toFahrenheit()
        }


        tempMin.text = context.getString(R.string.degrees, min.toInt())
        tempMax.text = context.getString(R.string.degrees, max.toInt())

        itemView.setOnClickListener { onClick?.invoke(entry) }
    }

    companion object {
        fun inflate(parent: ViewGroup):
                DayHolder = DayHolder(inflateView(R.layout.weather_data_day_item, parent, false))
    }
}