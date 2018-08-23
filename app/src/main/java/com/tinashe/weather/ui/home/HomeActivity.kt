package com.tinashe.weather.ui.home

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.tinashe.weather.R
import com.tinashe.weather.injection.ViewModelFactory
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.model.event.PhotoEvent
import com.tinashe.weather.ui.about.AppInfoActivity
import com.tinashe.weather.ui.base.BillingAwareActivity
import com.tinashe.weather.ui.home.detail.DetailFragment
import com.tinashe.weather.ui.home.place.PlaceForecastActivity
import com.tinashe.weather.ui.splash.SplashActivity
import com.tinashe.weather.utils.*
import com.tinashe.weather.utils.prefs.AppPrefs
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import javax.inject.Inject


class HomeActivity : BillingAwareActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var prefs: AppPrefs

    private lateinit var viewModel: HomeViewModel

    private lateinit var dataAdapter: WeatherDataAdapter

    private var mGeoDataClient: GeoDataClient? = null

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

        viewModel.viewState.observe(this, Observer { data ->

            data?.let { it ->
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

                        it.errorMessage?.let { msg ->
                            errorMessage.text = msg

                            if (dataAdapter.itemCount == 0) {
                                errorMessage.show()
                            } else {
                                Snackbar.make(errorMessage, msg, Snackbar.LENGTH_INDEFINITE)
                                        .setAction(android.R.string.ok) { }
                                        .setActionTextColor(Color.YELLOW)
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
                    else -> {
                    }
                }
            }
        })

        viewModel.latestForecast.observe(this, Observer { forecast ->
            forecast?.let {
                currentName.text = it.currently.location

                val animate = dataAdapter.itemCount == 0
                dataAdapter.forecast = it
                if (animate) {
                    listView.scheduleLayoutAnimation()
                }
            }
        })

        viewModel.promotePremium.observe(this, Observer {
            if (it == true) {
                promotePremium()
            }
        })

        viewModel.savedPlaces.observe(this, Observer { places ->
            places?.let {
                dataAdapter.savedPlaces = it

                it.map { getPhoto(it.placeId) }
            }
        })

        initUi()

    }

    private fun initUi() {
        setSupportActionBar(toolbar)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.icon_tint))
        toolbar.setNavigationOnClickListener {
            if (!viewModel.hasPremium()) {
                promotePremium()
                return@setNavigationOnClickListener
            }

            try {
                val typeFilter = AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                        .build()

                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                        .setFilter(typeFilter)
                        .build(this)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: GooglePlayServicesRepairableException) {
                Timber.e(e)
                Crashlytics.logException(e)
            } catch (e: GooglePlayServicesNotAvailableException) {
                Timber.e(e)
                Crashlytics.logException(e)
            }
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
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val place = PlaceAutocomplete.getPlace(this, data!!)
                    Timber.i("Place: %s:%s", place.name, place.latLng)

                    PlaceForecastActivity.view(this, place.id)
                }
                PlaceAutocomplete.RESULT_ERROR -> {
                    val status = PlaceAutocomplete.getStatus(this, data!!)
                    Timber.e(status.statusMessage)

                    status.statusMessage?.let {
                        Snackbar.make(toolbar, it, Snackbar.LENGTH_LONG)
                                .show()
                    }

                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }


    private fun getPhoto(placeId: String) {
        if (mGeoDataClient == null) {
            mGeoDataClient = Places.getGeoDataClient(this)
        }

        if (BitmapCache.getInstance().exists(placeId)) {
            return
        }

        mGeoDataClient?.getPlacePhotos(placeId)
                ?.addOnCompleteListener { task ->

                    val response = task.result.photoMetadata

                    if (response.count == 0) {
                        response.release()
                        return@addOnCompleteListener
                    }

                    mGeoDataClient?.getPhoto(response.first())
                            ?.addOnCompleteListener {
                                val photo = it.result.bitmap
                                BitmapCache.getInstance().add(placeId, photo)

                                RxBus.getInstance().send(PhotoEvent(placeId, photo))
                            }

                    response.release()
                }
    }

    companion object {
        private const val LOC_PERM = Manifest.permission.ACCESS_FINE_LOCATION
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1
    }
}
