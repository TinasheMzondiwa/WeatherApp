package com.tinashe.weather.ui.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.tinashe.weather.R
import com.tinashe.weather.injection.ViewModelFactory
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.ui.home.HomeActivity
import com.tinashe.weather.utils.WeatherUtil
import com.tinashe.weather.utils.getViewModel
import com.tinashe.weather.utils.hide
import com.tinashe.weather.utils.show
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by tinashe on 2018/03/20.
 */
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: SplashViewModel

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Timber.d("onLocationResult, $locationResult")

            locationResult.lastLocation?.let {
                viewModel.locationReceived(it, WeatherUtil.getLocationName(this@SplashActivity, it))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_splash)

        viewModel = getViewModel(this, viewModelFactory)
        viewModel.viewState.observe(this, Observer {

            it?.let {
                when (it.state) {
                    ViewState.LOADING -> {
                        rationalView.hide()
                        progressBar.show()
                    }
                    ViewState.ERROR -> {
                        progressBar.hide()
                        rationalView.show()
                    }
                    ViewState.SUCCESS -> {

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                }
            }

        })

        btnRequestPerms.setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(LOC_PERM), RQ_LOCATION)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.subscribe()

        if (ActivityCompat.checkSelfPermission(this, LOC_PERM) == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        } else {
            viewModel.permissionsNotGranted()
        }
    }

    override fun onStop() {
        viewModel.unSubscribe()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient?.lastLocation?.addOnSuccessListener {
            if (it == null) {
                val locationRequest = LocationRequest()
                locationRequest.interval = 30000
                locationRequest.fastestInterval = 30000
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

            } else {
                viewModel.locationReceived(it, WeatherUtil.getLocationName(this, it))
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {

        if (requestCode == RQ_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission has been granted
                fetchLocation()
            } else {
                viewModel.permissionsNotGranted()
                Snackbar.make(progressBar, R.string.permissions_not_granted, Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }

    companion object {
        private const val RQ_LOCATION: Int = 23
        private const val LOC_PERM = Manifest.permission.ACCESS_FINE_LOCATION
    }
}