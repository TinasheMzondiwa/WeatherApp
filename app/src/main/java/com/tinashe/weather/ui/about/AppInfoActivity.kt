package com.tinashe.weather.ui.about

import android.os.Bundle
import android.view.Menu
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import com.tinashe.weather.R
import com.tinashe.weather.data.di.ViewModelFactory
import com.tinashe.weather.data.model.TemperatureUnit
import com.tinashe.weather.data.model.ThemeStyle
import com.tinashe.weather.extensions.getViewModel
import com.tinashe.weather.extensions.observeNonNull
import com.tinashe.weather.extensions.vertical
import com.tinashe.weather.ui.base.BillingAwareActivity
import com.tinashe.weather.utils.WeatherUtil
import com.tinashe.weather.utils.prefs.AppPrefs
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_about)

        viewModel = getViewModel(this, viewModelFactory)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listAdapter = ItemsListAdapter()

        itemsList.apply {
            vertical(true)
            isNestedScrollingEnabled = false
            adapter = listAdapter
        }

        viewModel.infoItems.observeNonNull(this) {
            listAdapter.items = it
        }

        applySettings()
    }

    private fun applySettings() {

        tempUnitsContainer.setOnClickListener {
            val checked = when (prefs.getTemperatureUnit()) {
                TemperatureUnit.CELSIUS -> 0
                TemperatureUnit.FAHRENHEIT -> 1
            }

            val optionsArray = arrayOf(getString(R.string.celsius), getString(R.string.fahrenheit))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_about, menu)

        val toggleTheme = toolbar.menu?.findItem(R.id.menu_theme)
        val toggle = toggleTheme?.actionView
        if (toggle is CheckBox) {
            toggle.setButtonDrawable(R.drawable.avd_theme)
            toggle.isChecked = prefs.getThemeStyle() == ThemeStyle.DARK_MODE
            toggle.jumpDrawablesToCurrentState()
            toggle.setOnCheckedChangeListener { _, isChecked ->

                toggle.postDelayed({
                    val style = if (isChecked) ThemeStyle.DARK_MODE else ThemeStyle.LIGHT_MODE

                    prefs.setThemeStyle(style)
                    WeatherUtil.applyTheme(style)

                }, 800L)
            }
        }

        return super.onCreateOptionsMenu(menu)
    }
}