package com.tinashe.weather.ui.home

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.tinashe.weather.model.Entry
import com.tinashe.weather.model.Forecast
import com.tinashe.weather.model.SavedPlace
import com.tinashe.weather.model.event.WeatherEvent
import com.tinashe.weather.ui.home.vh.AttributionHolder
import com.tinashe.weather.ui.home.vh.CurrentDayHolder
import com.tinashe.weather.ui.home.vh.DayHolder
import com.tinashe.weather.ui.home.vh.SavedPlacesHolder
import com.tinashe.weather.utils.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by tinashe on 2018/03/21.
 */
class WeatherDataAdapter constructor(private val onDayClick: (Entry) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var size: Int = 0
    private var daily: List<Entry> = mutableListOf()

    private val disposables = CompositeDisposable()

    init {
        val weather = RxBus.getInstance().toObservable(WeatherEvent::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val id = it.placeId
                    savedPlaces.find { it.placeId == id }?.entry = it.entry
                }, {
                    Timber.e(it)
                })

        disposables.add(weather)
    }

    var forecast: Forecast? = null
        set(value) {
            field = value
            daily = value?.daily?.data ?: mutableListOf()

            //current + daily + attribution
            size = 1 + daily.size + 1

            if (savedPlaces.isNotEmpty()) {
                size += 1
            }

            notifyDataSetChanged()
        }

    var savedPlaces: ArrayList<SavedPlace> = arrayListOf()
        set(value) {
            val empty = field.isEmpty()
            field = value

            if (size == 0) {
                return
            }

            if (empty && value.isNotEmpty()) {
                size += 1
                notifyItemInserted(size - 2)
            } else if (value.isNotEmpty()) {
                notifyItemChanged(size - 2)
            } else if (value.isEmpty() && !empty) {
                size--
                notifyDataSetChanged()
            }
        }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_CURRENT
            size - 1 -> TYPE_ATTRIBUTION
            else -> {
                if (savedPlaces.isNotEmpty() && position == (size - 2)) {
                    TYPE_SAVED_PLACES
                } else {
                    TYPE_DAY
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CURRENT -> CurrentDayHolder.inflate(parent)
            TYPE_DAY -> DayHolder.inflate(parent)
            TYPE_SAVED_PLACES -> SavedPlacesHolder.inflate(parent)
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
            is SavedPlacesHolder -> holder.bind(savedPlaces)
        }
    }

    companion object {
        const val TYPE_CURRENT = 1
        const val TYPE_DAY = 2
        const val TYPE_SAVED_PLACES = 3
        const val TYPE_ATTRIBUTION = 4
    }
}