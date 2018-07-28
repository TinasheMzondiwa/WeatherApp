package com.tinashe.weather.ui.about

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tinashe.weather.R
import com.tinashe.weather.model.InfoItem
import com.tinashe.weather.utils.hide
import com.tinashe.weather.utils.inflateView
import com.tinashe.weather.utils.show
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.info_item.*

class ItemsListAdapter : RecyclerView.Adapter<ItemsListAdapter.InfoHolder>() {

    var items = arrayListOf<InfoItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> APP
            else -> ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): InfoHolder = InfoHolder.inflate(parent, type)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: InfoHolder, position: Int) {
        holder.bind(items[position])
    }

    class InfoHolder constructor(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: InfoItem) {

            itemIcon.setImageResource(item.icon)

            itemTitle.text = item.title

            item.description?.let {
                itemDesc.text = it
                itemDesc.show()
            } ?: itemDesc.hide()

            itemView.setOnClickListener {
                item.link?.let {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(it)

                    intent.resolveActivity(itemView.context.packageManager)?.let {
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }

        companion object {
            fun inflate(parent: ViewGroup, type: Int):
                    InfoHolder = InfoHolder(inflateView(if (type == APP) R.layout.app_info_item
            else R.layout.info_item, parent, false))
        }

    }

    companion object {
        private const val APP = 1
        private const val ITEM = 2
    }
}