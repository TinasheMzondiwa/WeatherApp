package com.tinashe.weather.ui.home.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tinashe.weather.R
import com.tinashe.weather.data.di.ViewModelFactory
import com.tinashe.weather.data.model.DateFormat
import com.tinashe.weather.data.model.Entry
import com.tinashe.weather.data.model.ViewState
import com.tinashe.weather.extensions.*
import com.tinashe.weather.ui.base.BaseBottomSheetDialogFragment
import com.tinashe.weather.utils.DateUtil
import com.tinashe.weather.utils.prefs.AppPrefs
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_detail.view.*
import javax.inject.Inject

class DetailFragment : BaseBottomSheetDialogFragment() {

    private lateinit var entry: Entry

    private lateinit var viewModel: DetailViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var prefs: AppPrefs

    private val hoursAdapter: HoursAdapter = HoursAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(contentView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(contentView, savedInstanceState)
        AndroidSupportInjection.inject(this)

        entry = arguments?.getParcelable(ARG_ENTRY) ?: return

        contentView.entryDate.text = DateUtil.getFormattedDate(entry.time, DateFormat.DAY, entry.timeZone)
        contentView.entrySummary.text = entry.summary

        viewModel = getViewModel(this, viewModelFactory)
        viewModel.hourlyData.observeNonNull(this) {
            contentView.progressBar.hide()

            val animate = hoursAdapter.itemCount == 0
            hoursAdapter.entries = it.data.toMutableList()
            if (animate) {
                contentView.listView.scheduleLayoutAnimation()
            }

        }
        viewModel.viewState.observeNonNull(this) { data ->
            when (data.state) {
                ViewState.SUCCESS -> contentView.errorView.hide()
                ViewState.LOADING -> {
                    contentView.errorView.hide()
                    contentView.progressBar.show()
                }
                ViewState.ERROR -> {
                    contentView.progressBar.hide()
                    data.message?.let {
                        contentView.errorView.text = it
                        contentView.errorView.show()
                    }
                }
            }
        }

        hoursAdapter.temperatureUnit = prefs.getTemperatureUnit()
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

        private const val ARG_ENTRY = "arg:entry"

        fun view(entry: Entry): DetailFragment {

            return DetailFragment().apply {
                arguments = bundleOf(Pair(ARG_ENTRY, entry))
            }
        }
    }
}