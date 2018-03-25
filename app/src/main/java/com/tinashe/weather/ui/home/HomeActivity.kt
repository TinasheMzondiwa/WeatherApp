package com.tinashe.weather.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tinashe.weather.R
import com.tinashe.weather.injection.ViewModelFactory
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.ui.base.BaseThemedActivity
import com.tinashe.weather.ui.splash.SplashActivity
import com.tinashe.weather.utils.WeatherUtil
import com.tinashe.weather.utils.getViewModel
import com.tinashe.weather.utils.hide
import com.tinashe.weather.utils.vertical
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.include_weather_today.*
import javax.inject.Inject

class HomeActivity : BaseThemedActivity() {

    companion object {
        private const val LOC_PERM = Manifest.permission.ACCESS_FINE_LOCATION
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: HomeViewModel

    private val dataAdapter: WeatherDataAdapter = WeatherDataAdapter()

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

        viewModel.viewState.observe(this, Observer {

            it?.let {
                when (it.state) {
                    ViewState.LOADING -> {
                        refreshLayout.isRefreshing = true
                    }
                    ViewState.ERROR -> {
                        refreshLayout.isRefreshing = false
                        progressBar.hide()

                        it.errorMessage?.let {
                            Snackbar.make(toolbar, it, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(android.R.string.ok, { })
                                    .show()
                        }
                    }
                    ViewState.SUCCESS -> {
                        refreshLayout.isRefreshing = false
                        progressBar.hide()

                    }
                    else -> {
                    }
                }
            }

        })

        viewModel.latestForecast.observe(this, Observer {
            it?.let {
                currentName.text = it.currently.location
                currentTemperature.text = getString(R.string.degrees, it.currently.temperature.toInt())
                currentSummary.text = it.currently.summary
                WeatherUtil.getIcon(this, it.currently.icon)?.let {
                    currentIcon.setImageDrawable(it)
                }

                val animate = dataAdapter.itemCount == 0
                dataAdapter.forecast = it
                if (animate) {
                    listView.scheduleLayoutAnimation()
                }
            }
        })

        initUi()

    }

    private fun initUi() {
        setSupportActionBar(toolbar)

        refreshLayout.setColorSchemeResources(R.color.theme)
        refreshLayout.setOnRefreshListener { viewModel.refreshForecast() }

        listView.apply {
            vertical()
            adapter = dataAdapter
            addOnScrollListener(appBarElevation)
        }
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(this, LOC_PERM) == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        } else {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {

        val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {

            viewModel.subscribe(it, WeatherUtil.getLocationName(this, it))
        }

    }

    override fun onStop() {
        viewModel.unSubscribe()
        super.onStop()
    }
}
