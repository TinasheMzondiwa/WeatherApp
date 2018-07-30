package com.tinashe.weather.ui.about

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import com.tinashe.weather.R
import com.tinashe.weather.injection.ViewModelFactory
import com.tinashe.weather.ui.base.BillingAwareActivity
import com.tinashe.weather.utils.getViewModel
import com.tinashe.weather.utils.vertical
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_about.*
import javax.inject.Inject

class AppInfoActivity : BillingAwareActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

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

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_line)!!)

        itemsList.apply {
            vertical()
            addItemDecoration(dividerItemDecoration)
            adapter = listAdapter
        }

        viewModel.infoItems.observe(this, Observer {
            it?.let {
                listAdapter.items = it
            }
        })
    }

    override fun premiumUnlocked() {

    }
}