package com.tinashe.weather.extensions

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tinashe.weather.R

/**
 * Created by tinashe on 2018/03/20.
 */

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun RecyclerView.vertical(divider: Boolean = false) {
    this.layoutManager = LinearLayoutManager(context)

    if (divider) {
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        val drawable = ContextCompat.getDrawable(context, R.drawable.divider_line) ?: return
        dividerItemDecoration.setDrawable(drawable)

        addItemDecoration(dividerItemDecoration)
    }
}

fun RecyclerView.horizontal() {
    this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
}

fun inflateView(@LayoutRes layoutResId: Int, parent: ViewGroup, attachToRoot: Boolean): View =
        LayoutInflater.from(parent.context).inflate(layoutResId, parent, attachToRoot)


fun Drawable.tint(color: Int) {
    DrawableCompat.wrap(this)
    DrawableCompat.setTint(this, color)
    DrawableCompat.unwrap<Drawable>(this)
}