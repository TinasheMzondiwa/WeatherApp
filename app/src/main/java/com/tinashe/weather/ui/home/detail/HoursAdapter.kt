package com.tinashe.weather.ui.home.detail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.weather.data.model.Entry
import com.tinashe.weather.data.model.TemperatureUnit
import com.tinashe.weather.ui.home.detail.vh.HourHolder

class HoursAdapter : RecyclerView.Adapter<HourHolder>() {

    var temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS

    var entries = mutableListOf<Entry>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourHolder = HourHolder.inflate(parent)

    override fun getItemCount(): Int = entries.size

    override fun onBindViewHolder(holder: HourHolder, position: Int) {
        holder.bind(entries[position], temperatureUnit)
    }
}