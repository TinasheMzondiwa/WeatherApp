package com.tinashe.weather.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.tinashe.weather.BuildConfig
import com.tinashe.weather.R
import com.tinashe.weather.data.model.Entry
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import timber.log.Timber
import java.util.*


/**
 * Created by tinashe on 2018/03/20.
 */
object WeatherUtil {

    fun getIconRes(name: String): Int {
        return when {
            name.contains("partly-cloudy", true) -> R.drawable.cloud_sun
            name.containsEither("clear", "sun") -> R.drawable.sun
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

    }

    fun getIcon(context: Context, name: String): Drawable? {
        val resources = context.resources

        return try {
            val resourceId = resources.getIdentifier(name, "drawable", context.packageName)
            ResourcesCompat.getDrawable(resources, resourceId, null) ?: throw Exception("")
        } catch (ex: Throwable) {

            ContextCompat.getDrawable(context, getIconRes(name))
        }
    }

    fun getLocationName(context: Context, location: Location): String {
        try {
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
        } catch (ex: Exception) {
            Timber.e(ex)
            return ""
        }
    }

    fun getBackground(entry: Entry): String {

        val time = LocalDateTime.ofInstant(Instant.ofEpochSecond(entry.time), ZoneId.of(entry.timeZone))
        val sunset = LocalDateTime.ofInstant(Instant.ofEpochSecond(entry.sunsetTime), ZoneId.of(entry.timeZone))
        val sunrise = LocalDateTime.ofInstant(Instant.ofEpochSecond(entry.sunriseTime), ZoneId.of(entry.timeZone))

        val isNightMode = time.isAfter(sunset) || time.isBefore(sunrise)

        return when {
            entry.icon.containsEither("sun", "clear") -> {
                if (isNightMode) BuildConfig.CLEAR_NIGHT else BuildConfig.CLEAR_DAY
            }
            entry.icon.containsEither("cloud") -> {
                if (isNightMode) BuildConfig.CLOUD_NIGHT else BuildConfig.CLOUD_DAY
            }
            entry.icon.containsEither("drizzle", "rain") -> {
                if (isNightMode) BuildConfig.RAIN_NIGHT else BuildConfig.RAIN_DAY
            }
            entry.icon.containsEither("lightning") -> {
                if (isNightMode) BuildConfig.LIGHTNING_NIGHT else BuildConfig.LIGHTNING_DAY
            }
            entry.icon.containsEither("snow") -> {
                if (isNightMode) BuildConfig.SNOW_NIGHT else BuildConfig.SNOW_DAY
            }
            else -> {
                if (isNightMode) BuildConfig.CLEAR_NIGHT else BuildConfig.CLEAR_DAY
            }
        }
    }

    fun hasConnection(context: Context): Boolean {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo

        return activeNetwork != null && activeNetwork.isConnected
    }
}