package com.tinashe.weather.ui.home.detail

import android.annotation.SuppressLint
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tinashe.weather.R
import com.tinashe.weather.injection.ViewModelFactory
import com.tinashe.weather.model.DateFormat
import com.tinashe.weather.model.Entry
import com.tinashe.weather.model.ViewState
import com.tinashe.weather.ui.base.BaseBottomSheetDialogFragment
import com.tinashe.weather.utils.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_detail.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DetailFragment : BaseBottomSheetDialogFragment() {

    private lateinit var entry: Entry

    private lateinit var viewModel: DetailViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val hoursAdapter: HoursAdapter = HoursAdapter()

    override fun layoutRes(): Int = R.layout.fragment_detail

    @SuppressLint("MissingPermission")
    override fun initialize(contentView: View) {
        AndroidSupportInjection.inject(this)

        val date = Date(TimeUnit.MILLISECONDS.convert(entry.time, TimeUnit.SECONDS))
        contentView.entryDate.text = DateUtil.getFormattedDate(date, DateFormat.DAY)
        contentView.entrySummary.text = entry.summary

        viewModel = getViewModel(this, viewModelFactory)
        viewModel.hourlyData.observe(this, android.arch.lifecycle.Observer {
            contentView.progressBar.hide()

            it?.let {
                val animate = hoursAdapter.itemCount == 0
                hoursAdapter.entries = it.data.toMutableList()
                if (animate) {
                    contentView.listView.scheduleLayoutAnimation()
                }
            }

        })
        viewModel.viewState.observe(this, android.arch.lifecycle.Observer {
            it?.let {
                when(it.state){
                    ViewState.SUCCESS -> contentView.errorView.hide()
                    ViewState.LOADING -> {
                        contentView.errorView.hide()
                        contentView.progressBar.show()
                    }
                    ViewState.ERROR -> {
                        contentView.progressBar.hide()
                        it.errorMessage?.let {
                            contentView.errorView.text = it
                            contentView.errorView.show()
                        }
                    }
                    else -> {}
                }
            }
        })

        contentView.listView.apply {
            horizontal()
            adapter = hoursAdapter
        }

        val fusedLocationClient: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(contentView.context)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            viewModel.getForeCast(entry, it)
        }
    }

    companion object {
        fun view(entry: Entry): DetailFragment {
            val fragment = DetailFragment()
            fragment.entry = entry

            return fragment
        }
    }
}