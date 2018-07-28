package com.tinashe.weather.ui.home.vh

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tinashe.weather.R
import com.tinashe.weather.model.DateFormat
import com.tinashe.weather.model.Entry
import com.tinashe.weather.utils.DateUtil
import com.tinashe.weather.utils.WeatherUtil
import com.tinashe.weather.utils.inflateView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.weather_hour_item.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by tinashe on 2018/03/21.
 */
class HourHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(entry: Entry) {
        val context = itemView.context

        val date = Date(TimeUnit.MILLISECONDS.convert(entry.time, TimeUnit.SECONDS))
        val now = Calendar.getInstance()
        now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + 1)

        val diff = now.timeInMillis - date.time
        val minutes = TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)

        hourTime.text = if (minutes > 0) {
            context.getString(R.string.now)
        } else {
            DateUtil.getFormattedDate(date, DateFormat.TIME_SHORT)
        }
        hourIcon.setImageDrawable(WeatherUtil.getIcon(context, entry.icon))
        hourTemperature.text = context.getString(R.string.degrees, entry.temperature.toInt())
    }

    companion object {
        fun inflate(parent: ViewGroup):
                HourHolder = HourHolder(inflateView(R.layout.weather_hour_item, parent, false))
    }
}