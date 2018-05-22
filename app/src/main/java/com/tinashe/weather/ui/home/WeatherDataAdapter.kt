package com.tinashe.weather.ui.home

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.tinashe.weather.model.Entry
import com.tinashe.weather.model.Forecast
import com.tinashe.weather.ui.home.vh.AttributionHolder
import com.tinashe.weather.ui.home.vh.CurrentDayHolder
import com.tinashe.weather.ui.home.vh.DayHolder

/**
 * Created by tinashe on 2018/03/21.
 */
class WeatherDataAdapter constructor(private val onDayClick: (Entry) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_CURRENT = 1
        const val TYPE_DAY = 2
        const val TYPE_ATTRIBUTION = 3
    }

    private var size: Int = 0
    private var daily: List<Entry> = mutableListOf()

    var forecast: Forecast? = null
        set(value) {
            field = value
            daily = value?.daily?.data ?: mutableListOf()

            //current + daily + attribution
            size = 1 + daily.size + 1

            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_CURRENT
            size - 1 -> TYPE_ATTRIBUTION
            else -> TYPE_DAY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CURRENT -> CurrentDayHolder.inflate(parent)
            TYPE_DAY -> DayHolder.inflate(parent)
            else -> AttributionHolder.inflate(parent)
        }
    }

    override fun getItemCount(): Int = size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is CurrentDayHolder -> forecast?.let {
                holder.bind(it.currently, it.hourly)
            }
            is DayHolder -> {
                val dayPos = position - 1

                if (dayPos == 0) {
                    holder.bind(daily[dayPos])
                } else {
                    holder.bind(daily[dayPos], onClick = {
                        onDayClick.invoke(it)
                    })
                }
            }
            is AttributionHolder -> holder.bind()
        }
    }
}