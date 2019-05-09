package com.tinashe.weather.utils

import android.graphics.Bitmap
import androidx.collection.LruCache

object BitmapCache {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8
    private var memoryCache: LruCache<String, Bitmap> = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.byteCount / 1024
        }
    }

    fun add(key: String, bitmap: Bitmap) {
        if (get(key) == null) {
            memoryCache.put(key, bitmap)
        }
    }

    fun get(key: String): Bitmap? {
        return memoryCache.get(key)
    }

    fun exists(key: String): Boolean = get(key) != null
}