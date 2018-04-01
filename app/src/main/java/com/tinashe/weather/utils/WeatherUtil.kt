package com.tinashe.weather.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import com.tinashe.weather.R
import java.util.*


/**
 * Created by tinashe on 2018/03/20.
 */
object WeatherUtil {

    fun getIcon(context: Context, name: String): Drawable? {
        val resources = context.resources

        return try {
            val resourceId = resources.getIdentifier(name, "drawable", context.packageName)
            ResourcesCompat.getDrawable(resources, resourceId, null) ?: throw Exception("")
        } catch (ex: Throwable) {
            val res = when {
                name.contains("sun", true) -> R.drawable.sun
                name.contains("partly-cloudy", true) -> R.drawable.cloud_sun
                name.contains("clear", true) -> R.drawable.sun
                name.contains("rain", true) -> R.drawable.cloud_rain
                name.contains("snow", true) -> R.drawable.cloud_snow
                name.contains("fog", true) -> R.drawable.cloud_fog
                name.contains("wind", true) -> R.drawable.wind
                name.contains("hail", true) -> R.drawable.cloud_hail
                name.contains("drizzle", true) -> R.drawable.cloud_drizzle
                name.contains("lightning", true) -> R.drawable.cloud_lightning
                name.contains("tornado", true) -> R.drawable.tornado
                else -> R.drawable.cloud
            }

            ContextCompat.getDrawable(context, res)
        }
    }

    fun getLocationName(context: Context, location: Location): String {
        val gcd = Geocoder(context, Locale.getDefault())
        val addresses = gcd.getFromLocation(location.latitude, location.longitude, 1)
        return if (addresses.size > 0) {
            val subLocality = addresses[0].subLocality
            val locality = addresses[0].locality

            if (subLocality.isNullOrEmpty() || subLocality == locality) {
                locality
            } else {
                "$subLocality, $locality"
            }

        } else {
            ""
        }
    }

    fun getWeatherTheme(summary: String): Int {

        return when {
            summary.contains("sunny", true) -> R.color.sunny
            summary.contains("cloudy", true) -> R.color.cloudy
            summary.contains("wind", true) -> R.color.windy
            else -> R.color.clear
        }
    }

    fun hasConnection(context: Context) : Boolean {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}