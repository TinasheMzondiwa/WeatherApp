package com.tinashe.weather.ui.about

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.weather.R
import com.tinashe.weather.data.model.InfoItem
import com.tinashe.weather.ui.about.vh.InfoHolder

class ItemsListAdapter : RecyclerView.Adapter<InfoHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): InfoHolder {
        val layoutRes = if (type == APP) {
            R.layout.app_info_item
        } else {
            R.layout.info_item
        }

        return InfoHolder.inflate(parent, layoutRes)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: InfoHolder, position: Int) {
        holder.bind(items[position])
    }

    companion object {
        private const val APP = 1
        private const val ITEM = 2
    }
}