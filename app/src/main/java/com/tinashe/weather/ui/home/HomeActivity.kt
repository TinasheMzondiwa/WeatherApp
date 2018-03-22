package com.tinashe.weather.ui.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.tinashe.weather.R
import com.tinashe.weather.injection.ViewModelFactory
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.utils.WeatherUtil
import com.tinashe.weather.utils.getViewModel
import com.tinashe.weather.utils.hide
import com.tinashe.weather.utils.vertical
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.include_weather_today.*
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

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

                dataAdapter.forecast = it
            }
        })

        initUi()

    }

    private fun initUi() {
        setSupportActionBar(toolbar)

        refreshLayout.setColorSchemeResources(R.color.theme)
        refreshLayout.setOnRefreshListener { viewModel.refreshForecast() }

        listView.vertical()
        dataAdapter = WeatherDataAdapter()
        listView.adapter = dataAdapter
        listView.addOnScrollListener(appBarElevation)
    }

    override fun onStart() {
        super.onStart()
        viewModel.subscribe()
    }

    override fun onStop() {
        viewModel.unSubscribe()
        super.onStop()
    }
}
