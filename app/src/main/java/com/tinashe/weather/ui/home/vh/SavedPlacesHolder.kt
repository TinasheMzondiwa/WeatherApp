package com.tinashe.weather.ui.home.vh

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.weather.R
import com.tinashe.weather.data.model.SavedPlace
import com.tinashe.weather.data.model.TemperatureUnit
import com.tinashe.weather.data.model.event.PhotoEvent
import com.tinashe.weather.data.model.event.WeatherEvent
import com.tinashe.weather.extensions.horizontal
import com.tinashe.weather.extensions.inflateView
import com.tinashe.weather.extensions.toFahrenheit
import com.tinashe.weather.ui.home.place.PlaceForecastActivity
import com.tinashe.weather.utils.BitmapCache
import com.tinashe.weather.utils.RxBus
import com.tinashe.weather.utils.WeatherUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.saved_place_item.*
import kotlinx.android.synthetic.main.saved_places_item.*
import timber.log.Timber

class SavedPlacesHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(places: ArrayList<SavedPlace>, unit: TemperatureUnit) {
        placesList.apply {
            horizontal()
            adapter = PlacesAdapter(places, unit)
        }
    }

    inner class PlacesAdapter constructor(private val places: ArrayList<SavedPlace>,
                                    private val unit: TemperatureUnit) : RecyclerView.Adapter<PlaceHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PlaceHolder {
            return PlaceHolder.inflate(parent)
        }

        override fun getItemCount(): Int = places.size

        override fun onBindViewHolder(holder: PlaceHolder, p: Int) {
            holder.bind(places[p], unit)
        }
    }

    class PlaceHolder constructor(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val disposables = CompositeDisposable()
        private var place: SavedPlace? = null
        private var unit: TemperatureUnit = TemperatureUnit.CELSIUS

        init {
            val weather = RxBus.getInstance().toObservable(WeatherEvent::class.java)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (place?.placeId == it.placeId) {
                            place?.entry = it.entry

                            placeIcon.setImageResource(WeatherUtil.getIconRes(it.entry.icon))

                            val temp = when (unit) {
                                TemperatureUnit.FAHRENHEIT -> it.entry.temperature.toFahrenheit()
                                else -> it.entry.temperature
                            }

                            placeTemp.text = itemView.context.getString(R.string.degrees, temp.toInt())
                            placeSummary.text = it.entry.summary
                        }
                    }, {
                        Timber.e(it)
                    })

            val photo = RxBus.getInstance().toObservable(PhotoEvent::class.java)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        if (place?.placeId == it.placeId) {
                            place?.photo = it.photo
                            placeImg.setImageBitmap(BitmapCache.get(it.placeId))
                        }

                    }, {
                        Timber.e(it)
                    })

            disposables.addAll(weather, photo)
        }

        fun bind(place: SavedPlace, unit: TemperatureUnit) {
            this.place = place
            this.unit = unit

            placeName.text = place.name
            place.entry?.let {
                placeIcon.setImageResource(WeatherUtil.getIconRes(it.icon))

                val temp = when (unit) {
                    TemperatureUnit.FAHRENHEIT -> it.temperature.toFahrenheit()
                    else -> it.temperature
                }

                placeTemp.text = itemView.context.getString(R.string.degrees, temp.toInt())
                placeSummary.text = it.summary
            }

            if (BitmapCache.exists(place.placeId)) {
                placeImg.setImageBitmap(BitmapCache.get(place.placeId))
            } else {
                place.photo?.let {
                    placeImg.setImageBitmap(it)
                }
            }

            itemView.setOnClickListener {
                PlaceForecastActivity.view(it.context, place.placeId)
            }
        }

        companion object {
            fun inflate(parent: ViewGroup):
                    PlaceHolder = PlaceHolder(inflateView(R.layout.saved_place_item, parent, false))
        }

    }

    companion object {
        fun inflate(parent: ViewGroup):
                SavedPlacesHolder = SavedPlacesHolder(inflateView(R.layout.saved_places_item, parent, false))
    }
}