package com.tinashe.weather.ui.about.vh

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.weather.data.model.InfoItem
import com.tinashe.weather.extensions.hide
import com.tinashe.weather.extensions.inflateView
import com.tinashe.weather.extensions.show
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.info_item.*

class InfoHolder constructor(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(item: InfoItem) {

        itemIcon.setImageResource(item.icon)

        itemTitle.text = item.title

        item.description?.let {
            itemDesc.text = it
            itemDesc.show()
        } ?: itemDesc.hide()

        containerView.setOnClickListener {
            item.link?.let {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(it)
                }

                val context = containerView.context
                intent.resolveActivity(context.packageManager)?.let {
                    context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        fun inflate(parent: ViewGroup, @LayoutRes layoutRes: Int):
                InfoHolder = InfoHolder(inflateView(layoutRes, parent, false))
    }

}
