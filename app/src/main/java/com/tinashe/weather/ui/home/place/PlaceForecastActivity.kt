package com.tinashe.weather.ui.home.place

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import com.google.android.gms.location.places.Places
import com.tinashe.weather.R
import com.tinashe.weather.injection.ViewModelFactory
import com.tinashe.weather.ui.base.BaseThemedActivity
import com.tinashe.weather.ui.home.WeatherDataAdapter
import com.tinashe.weather.ui.home.detail.DetailFragment
import com.tinashe.weather.utils.getViewModel
import com.tinashe.weather.utils.prefs.AppPrefs
import com.tinashe.weather.utils.tint
import com.tinashe.weather.utils.vertical
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_place_forecast.*
import javax.inject.Inject

class PlaceForecastActivity : BaseThemedActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var prefs: AppPrefs

    private lateinit var viewModel: PlaceForecastViewModel

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
        setContentView(R.layout.activity_place_forecast)

        initUi()

        viewModel = getViewModel(this, viewModelFactory)
        viewModel.placeHolder.observe(this, Observer { place ->
            place?.let {
                title = it.name
            } ?: finish()
        })
        viewModel.forecast.observe(this, Observer { forecast ->
            forecast?.let {
                dataAdapter.forecast = it
            }
        })

        viewModel.viewState.observe(this, Observer { state ->
            state?.let { data ->
                data.errorMessage?.let { msg ->
                    Snackbar.make(fab, msg, Snackbar.LENGTH_SHORT)
                            .setAction(android.R.string.ok) { }
                            .setActionTextColor(Color.YELLOW)
                            .show()
                }
            }
        })

        viewModel.isBookmarked.observe(this, Observer { bookmarked ->
            bookmarked?.let {
                fab.setImageResource(when (it) {
                    true -> R.drawable.ic_bookmark
                    else -> R.drawable.bookmark_plus_outline
                })
            }
        })

        viewModel.initPlace(intent.getStringExtra(ARG_PLACE_ID), Places.getGeoDataClient(this))
    }

    private fun initUi() {

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon?.tint(ContextCompat.getColor(this, R.color.icon_tint))

        dataAdapter = WeatherDataAdapter {
            val fragment = DetailFragment.view(it)
            fragment.show(supportFragmentManager, fragment.tag)
        }
        dataAdapter.temperatureUnit = prefs.getTemperatureUnit()

        listView.apply {
            vertical()
            adapter = dataAdapter
            addOnScrollListener(appBarElevation)
        }

        fab.setOnClickListener {
            viewModel.bookmarkClicked()
        }
    }

    companion object {

        private const val ARG_PLACE_ID = "arg:place_id"

        fun view(context: Context, placeId: String) {
            val intent = Intent(context, PlaceForecastActivity::class.java)
            intent.putExtra(ARG_PLACE_ID, placeId)
            context.startActivity(intent)
        }
    }


}