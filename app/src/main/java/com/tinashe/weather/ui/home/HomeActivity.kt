package com.tinashe.weather.ui.home

import android.Manifest
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tinashe.weather.R
import com.tinashe.weather.injection.ViewModelFactory
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.ui.about.AppInfoActivity
import com.tinashe.weather.ui.base.BillingAwareActivity
import com.tinashe.weather.ui.home.detail.DetailFragment
import com.tinashe.weather.ui.splash.SplashActivity
import com.tinashe.weather.utils.*
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

class HomeActivity : BillingAwareActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: HomeViewModel

    private lateinit var dataAdapter: WeatherDataAdapter

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

                        if (dataAdapter.itemCount > 0) {
                            progressBar.hide()
                        } else {
                            progressBar.hide()
                        }
                    }
                    ViewState.ERROR -> {
                        refreshLayout.isRefreshing = false
                        progressBar.hide()

                        it.errorMessage?.let {
                            errorMessage.text = it

                            if (dataAdapter.itemCount == 0) {
                                errorMessage.show()
                            } else {
                                Snackbar.make(errorMessage, it, Snackbar.LENGTH_INDEFINITE)
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

        viewModel.latestForecast.observe(this, Observer {
            it?.let {
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

        initUi()

    }

    private fun initUi() {
        setSupportActionBar(toolbar)
        toolbar.overflowIcon?.setTint(ContextCompat.getColor(this, R.color.icon_tint))
        toolbar.setNavigationOnClickListener {
            if (!hasPremium()) {
                promotePremium()
                return@setNavigationOnClickListener
            }
            Snackbar.make(toolbar, "Search coming soon!!", Snackbar.LENGTH_SHORT).show()
        }

        refreshLayout.setColorSchemeResources(R.color.theme)
        refreshLayout.setOnRefreshListener { viewModel.refreshForecast(hasPremium()) }

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
            val fusedLocationClient: FusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener {

                viewModel.subscribe(it, WeatherUtil.getLocationName(this, it), hasPremium())
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
        viewModel.refreshForecast(hasPremium())
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

    companion object {
        private const val LOC_PERM = Manifest.permission.ACCESS_FINE_LOCATION
    }
}
