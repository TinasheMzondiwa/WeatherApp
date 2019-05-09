package com.tinashe.weather.ui.about

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tinashe.weather.BuildConfig
import com.tinashe.weather.R
import com.tinashe.weather.data.model.InfoItem
import javax.inject.Inject

class AboutInfoViewModel @Inject constructor() : ViewModel() {

    var infoItems = MutableLiveData<ArrayList<InfoItem>>()

    init {
        val items = arrayListOf<InfoItem>()

        var item = InfoItem(R.drawable.icon_round, "Lite Weather")
        item.description = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\nCopyright © 2018\nTinashe Mzondiwa"
        item.link = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        items.add(item)

        item = InfoItem(R.drawable.ic_email, "E-mail")
        item.link = "mailto:tmzon08@gmail.com?subject=Lite Weather"
        items.add(item)

        item = InfoItem(R.drawable.twitter, "Twitter")
        item.link = "https://twitter.com/TinasheMzondiwa"
        items.add(item)

        item = InfoItem(R.drawable.github_circle, "Source")
        item.link = "https://github.com/TinasheMzondiwa/WeatherApp"
        items.add(item)

        infoItems.value = items
    }
}