package com.tinashe.weather.ui.home.vh

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.tinashe.weather.R
import com.tinashe.weather.model.DateFormat
import com.tinashe.weather.model.Entry
import com.tinashe.weather.model.TemperatureUnit
import com.tinashe.weather.model.WeatherData
import com.tinashe.weather.utils.*
import com.tinashe.weather.utils.glide.GlideApp
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.weather_curr_day_item.*

/**
 * Created by tinashe on 2018/03/21.
 */
class CurrentDayHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    private val hoursAdapter: HoursAdapter = HoursAdapter()

    fun bind(current: Entry, hourly: WeatherData, @TemperatureUnit unit: String) {
        val context = itemView.context
        hoursAdapter.unit = unit

        GlideApp.with(context)
                .load(WeatherUtil.getBackground(current))
                .placeholder(R.color.theme)
                .error(R.color.theme)
                .transition(withCrossFade())
                .into(dayBackgroundImg)

        currentTime.text = DateUtil.getFormattedDate(current.time, DateFormat.TIME, current.timeZone)
        WeatherUtil.getIcon(itemView.context, current.icon)?.let {
            currentIcon.setImageDrawable(it)
        }

        val temp = when(unit){
            TemperatureUnit.FAHRENHEIT -> current.temperature.toFahrenheit()
            else -> current.temperature
        }
        currentTemperature.text = context.getString(R.string.degrees, temp.toInt())
        currentSummary.text = current.summary

        val animate = hoursAdapter.itemCount == 0
        listView.apply {
            horizontal()
            adapter = hoursAdapter
            hoursAdapter.entries = hourly.data.toMutableList()
            if (animate) {
                scheduleLayoutAnimation()
            }
        }
    }

    class HoursAdapter : RecyclerView.Adapter<HourHolder>() {

        @TemperatureUnit
        var unit: String = TemperatureUnit.CELSIUS

        var entries = mutableListOf<Entry>()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourHolder = HourHolder.inflate(parent)

        override fun getItemCount(): Int = entries.size

        override fun onBindViewHolder(holder: HourHolder, position: Int) {
            holder.bind(entries[position], unit)
        }

    }

    companion object {
        fun inflate(parent: ViewGroup):
                CurrentDayHolder = CurrentDayHolder(inflateView(R.layout.weather_curr_day_item, parent, false))
    }
}