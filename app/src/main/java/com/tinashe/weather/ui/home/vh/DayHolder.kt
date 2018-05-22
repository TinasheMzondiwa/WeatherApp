package com.tinashe.weather.ui.home.vh

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tinashe.weather.R
import com.tinashe.weather.model.DateFormat
import com.tinashe.weather.model.Entry
import com.tinashe.weather.utils.DateUtil
import com.tinashe.weather.utils.inflateView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.weather_data_day_item.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by tinashe on 2018/03/21.
 */
class DayHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    companion object {
        fun inflate(parent: ViewGroup):
                DayHolder = DayHolder(inflateView(R.layout.weather_data_day_item, parent, false))
    }

    fun bind(entry: Entry, onClick: ((Entry) -> Unit)? = null) {
        val context = itemView.context

        val date = Date(TimeUnit.MILLISECONDS.convert(entry.time, TimeUnit.SECONDS))
        dayDate.text = DateUtil.getFormattedDate(date, DateFormat.DAY)
        daySummary.text = entry.summary

        tempMin.text = context.getString(R.string.degrees, entry.temperatureMin.toInt())
        tempMax.text = context.getString(R.string.degrees, entry.temperatureHigh.toInt())

        itemView.setOnClickListener { onClick?.invoke(entry) }
    }
}