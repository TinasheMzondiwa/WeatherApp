package com.tinashe.weather.ui.home.place

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.material.snackbar.Snackbar
import com.tinashe.weather.R
import com.tinashe.weather.data.di.ViewModelFactory
import com.tinashe.weather.extensions.getViewModel
import com.tinashe.weather.extensions.observeNonNull
import com.tinashe.weather.extensions.tint
import com.tinashe.weather.extensions.vertical
import com.tinashe.weather.ui.base.BaseActivity
import com.tinashe.weather.ui.home.WeatherDataAdapter
import com.tinashe.weather.ui.home.detail.DetailFragment
import com.tinashe.weather.utils.prefs.AppPrefs
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_place_forecast.*
import javax.inject.Inject

class PlaceForecastActivity : BaseActivity() {

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
        viewModel.placeHolder.observeNonNull(this) {
            title = it.name
        }
        viewModel.forecast.observeNonNull(this) {
            dataAdapter.forecast = it
        }

        viewModel.viewState.observeNonNull(this) {
            it.message?.let { msg ->
                Snackbar.make(fab, msg, Snackbar.LENGTH_SHORT)
                        .setAction(android.R.string.ok) { }
                        .show()
            }
        }

        viewModel.isBookmarked.observeNonNull(this) {
            fab.setImageResource(when (it) {
                true -> R.drawable.ic_bookmark
                else -> R.drawable.bookmark_plus_outline
            })
        }

        viewModel.initPlace(intent.getStringExtra(ARG_PLACE_ID), Places.createClient(this))
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
            val intent = Intent(context, PlaceForecastActivity::class.java).apply {
                putExtra(ARG_PLACE_ID, placeId)
            }
            context.startActivity(intent)
        }
    }


}