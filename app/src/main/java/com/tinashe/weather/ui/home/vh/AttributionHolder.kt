package com.tinashe.weather.ui.home.vh

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.weather.R
import com.tinashe.weather.extensions.inflateView
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by tinashe on 2018/03/22.
 */
class AttributionHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    init {
        containerView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://darksky.net/poweredby/")
            }
            val context = it.context

            intent.resolveActivity(context.packageManager)?.let {
                context.startActivity(intent)
            }
        }
    }

    fun bind() {
        // listener set
    }

    companion object {
        fun inflate(parent: ViewGroup):
                AttributionHolder = AttributionHolder(inflateView(R.layout.item_attribution, parent, false))
    }
}