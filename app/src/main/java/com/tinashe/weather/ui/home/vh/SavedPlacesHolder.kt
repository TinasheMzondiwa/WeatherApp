package com.tinashe.weather.ui.home.vh

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tinashe.weather.R
import com.tinashe.weather.model.SavedPlace
import com.tinashe.weather.model.event.PhotoEvent
import com.tinashe.weather.model.event.WeatherEvent
import com.tinashe.weather.ui.home.place.PlaceForecastActivity
import com.tinashe.weather.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.saved_place_item.*
import kotlinx.android.synthetic.main.saved_places_item.*
import timber.log.Timber

class SavedPlacesHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(places: ArrayList<SavedPlace>) {
        placesList.apply {
            horizontal()
            adapter = PlacesAdapter(places)
        }
    }

    class PlacesAdapter constructor(private val places: ArrayList<SavedPlace>) : RecyclerView.Adapter<PlaceHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PlaceHolder {
            return PlaceHolder.inflate(parent)
        }

        override fun getItemCount(): Int = places.size

        override fun onBindViewHolder(holder: PlaceHolder, p: Int) {
            holder.bind(places[p])
        }
    }

    class PlaceHolder constructor(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val disposables = CompositeDisposable()
        private var place: SavedPlace? = null

        init {
            val weather = RxBus.getInstance().toObservable(WeatherEvent::class.java)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (place?.placeId == it.placeId) {
                            place?.entry = it.entry

                            placeIcon.setImageResource(WeatherUtil.getIconRes(it.entry.icon))

                            placeTemp.text = itemView.context.getString(R.string.degrees, it.entry.temperature.toInt())
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
                            placeImg.setImageBitmap(BitmapCache.getInstance().get(it.placeId))
                        }

                    }, {
                        Timber.e(it)
                    })

            disposables.addAll(weather, photo)
        }

        fun bind(place: SavedPlace) {
            this.place = place

            placeName.text = place.name
            place.entry?.let {
                placeIcon.setImageResource(WeatherUtil.getIconRes(it.icon))

                placeTemp.text = itemView.context.getString(R.string.degrees, it.temperature.toInt())
                placeSummary.text = it.summary
            }

            if (BitmapCache.getInstance().exists(place.placeId)) {
                placeImg.setImageBitmap(BitmapCache.getInstance().get(place.placeId))
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