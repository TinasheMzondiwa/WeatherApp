package com.tinashe.weather.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.tinashe.weather.R
import com.tinashe.weather.data.di.ViewModelFactory
import com.tinashe.weather.data.model.ViewState
import com.tinashe.weather.data.model.event.PhotoEvent
import com.tinashe.weather.extensions.*
import com.tinashe.weather.ui.about.AppInfoActivity
import com.tinashe.weather.ui.base.BillingAwareActivity
import com.tinashe.weather.ui.home.detail.DetailFragment
import com.tinashe.weather.ui.home.place.PlaceForecastActivity
import com.tinashe.weather.ui.splash.SplashActivity
import com.tinashe.weather.utils.BitmapCache
import com.tinashe.weather.utils.RxBus
import com.tinashe.weather.utils.WeatherUtil
import com.tinashe.weather.utils.prefs.AppPrefs
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class HomeActivity : BillingAwareActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var prefs: AppPrefs

    private lateinit var viewModel: HomeViewModel

    private lateinit var dataAdapter: WeatherDataAdapter

    private var placesClient: PlacesClient? = null

    private val appBarElevation = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val raiseTitleBar = dy > 0 || recyclerView.computeVerticalScrollOffset() != 0
            appBarLayout.isActivated = raiseTitleBar
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_home)

        viewModel = getViewModel(this, viewModelFactory)

        viewModel.viewState.observeNonNull(this) {
            when (it.state) {
                ViewState.LOADING -> {
                    refreshLayout.isRefreshing = true

                    if (dataAdapter.itemCount > 0) {
                        progressBar.hide()
                    } else {
                        progressBar.hide()
                    }
                }
                ViewState.ERROR -> {
                    refreshLayout.isRefreshing = false
                    progressBar.hide()

                    it.message?.let { msg ->
                        errorMessage.text = msg

                        if (dataAdapter.itemCount == 0) {
                            errorMessage.show()
                        } else {
                            Snackbar.make(errorMessage, msg, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(android.R.string.ok) { }
                                    .show()
                        }
                    }
                }
                ViewState.SUCCESS -> {
                    errorMessage.text = ""
                    errorMessage.hide()
                    refreshLayout.isRefreshing = false
                    progressBar.hide()

                }
            }
        }

        viewModel.latestForecast.observeNonNull(this) { forecast ->
            currentName.text = forecast.currently.location

            val animate = dataAdapter.itemCount == 0
            dataAdapter.forecast = forecast
            if (animate) {
                listView.scheduleLayoutAnimation()
            }
        }

        viewModel.promotePremium.observeNonNull(this) {
            if (it) {
                promotePremium()
            }
        }

        viewModel.savedPlaces.observeNonNull(this) { places ->
            dataAdapter.savedPlaces = places

            places.forEach { getPhoto(it.placeId) }
        }

        initUi()

    }

    private fun initUi() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            if (!viewModel.hasPremium()) {
                promotePremium()
                return@setNavigationOnClickListener
            }

            startActivityForResult(placePickerIntent(), RC_PLACE_AUTOCOMPLETE)
        }

        refreshLayout.setColorSchemeResources(R.color.theme)
        refreshLayout.setOnRefreshListener { viewModel.refreshForecast() }

        dataAdapter = WeatherDataAdapter {
            val fragment = DetailFragment.view(it)
            fragment.show(supportFragmentManager, fragment.tag)
        }

        listView.apply {
            vertical()
            adapter = dataAdapter
            addOnScrollListener(appBarElevation)
        }
    }

    /**
     * Returns a Place Picker intent that will return a [Place] object with these fields:
     * [Place.Field.ID], [Place.Field.NAME], [Place.Field.LAT_LNG], [Place.Field.ADDRESS]
     *
     * @return Intent
     */
    private fun placePickerIntent(): Intent {

        return Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))
                .build(this)
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(this, LOC_PERM) == PackageManager.PERMISSION_GRANTED) {

            dataAdapter.temperatureUnit = prefs.getTemperatureUnit()

            val fusedLocationClient: FusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener {

                viewModel.subscribe(it, WeatherUtil.getLocationName(this, it))
            }
        } else {
            // Request permissions
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
    }

    override fun onStop() {
        viewModel.unSubscribe()
        super.onStop()
    }

    override fun premiumUnlocked() {
        viewModel.premiumUnlocked()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_info -> {
                startActivity(Intent(this, AppInfoActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val place = Autocomplete.getPlaceFromIntent(data)
            Timber.i("Place: %s:%s", place.name, place.latLng)

            PlaceForecastActivity.view(this, place.id!!)
        }
    }


    private fun getPhoto(placeId: String) {
        if (placesClient == null) {
            placesClient = Places.createClient(this)
        }

        if (BitmapCache.exists(placeId)) {
            return
        }

        val placeFields = Arrays.asList(Place.Field.PHOTO_METADATAS)

        val request = FetchPlaceRequest.builder(placeId, placeFields)
                .build()

        placesClient?.fetchPlace(request)?.addOnSuccessListener { response ->
            val place = response.place

            if (place.photoMetadatas?.isEmpty() == null) {
                return@addOnSuccessListener
            }
            val photoMetadata = place.photoMetadatas?.first() ?: return@addOnSuccessListener


            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .build()
            placesClient?.fetchPhoto(photoRequest)?.addOnSuccessListener {
                val photo = it.bitmap

                BitmapCache.add(placeId, photo)

                RxBus.getInstance().send(PhotoEvent(placeId, photo))

            }?.addOnFailureListener {
                Timber.e(it)
            }
        }?.addOnFailureListener {
            Timber.e(it)
        }
    }

    companion object {
        private const val LOC_PERM = Manifest.permission.ACCESS_FINE_LOCATION
        private const val RC_PLACE_AUTOCOMPLETE = 1
    }
}
