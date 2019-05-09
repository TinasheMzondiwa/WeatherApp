package com.tinashe.weather.ui.about

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.tinashe.weather.R
import com.tinashe.weather.data.di.ViewModelFactory
import com.tinashe.weather.data.model.TemperatureUnit
import com.tinashe.weather.ui.base.BillingAwareActivity
import com.tinashe.weather.utils.getViewModel
import com.tinashe.weather.utils.prefs.AppPrefs
import com.tinashe.weather.utils.vertical
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_about.*
import javax.inject.Inject

class AppInfoActivity : BillingAwareActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var prefs: AppPrefs

    private lateinit var viewModel: AboutInfoViewModel

    private lateinit var listAdapter: ItemsListAdapter

    private var optionsArray = emptyArray<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_about)

        viewModel = getViewModel(this, viewModelFactory)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listAdapter = ItemsListAdapter()

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_line)!!)

        itemsList.apply {
            vertical()
            isNestedScrollingEnabled = false
            addItemDecoration(dividerItemDecoration)
            adapter = listAdapter
        }

        viewModel.infoItems.observe(this, Observer { items ->
            items?.let {
                listAdapter.items = it
            }
        })

        optionsArray = arrayOf(getString(R.string.celsius), getString(R.string.fahrenheit))
        tempUnitsContainer.setOnClickListener {
            val checked = when (prefs.getTemperatureUnit()) {
                TemperatureUnit.CELSIUS -> 0
                TemperatureUnit.FAHRENHEIT -> 1
                else -> -1
            }

            AlertDialog.Builder(this, R.style.Theme_Dialog)
                    .setTitle(R.string.temp_units)
                    .setSingleChoiceItems(optionsArray, checked) { dialog, position ->
                        prefs.setTemperatureUnit(when (position) {
                            0 -> TemperatureUnit.CELSIUS
                            else -> TemperatureUnit.FAHRENHEIT
                        })

                        setUnitsValue()

                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .create()
                    .show()
        }

        setUnitsValue()
    }

    private fun setUnitsValue() {
        tempUnitsValue.text = when (prefs.getTemperatureUnit()) {
            TemperatureUnit.FAHRENHEIT -> getString(R.string.fahrenheit)
            else -> getString(R.string.celsius)
        }
    }

    override fun premiumUnlocked() {

    }
}