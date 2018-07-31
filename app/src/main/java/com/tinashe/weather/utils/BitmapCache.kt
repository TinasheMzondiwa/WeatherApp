package com.tinashe.weather.utils

import android.graphics.Bitmap
import android.support.v4.util.LruCache

class BitmapCache private constructor() {

    private var memoryCache: LruCache<String, Bitmap>? = null

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.byteCount / 1024
            }
        }
    }

    fun add(key: String, bitmap: Bitmap) {
        if (get(key) == null) {
            memoryCache?.put(key, bitmap)
        }
    }

    fun get(key: String): Bitmap? {
        return memoryCache?.get(key)
    }

    fun exists(key: String): Boolean = get(key) != null

    companion object {

        private val lock = Any()

        private var instance: BitmapCache? = null

        fun getInstance(): BitmapCache {
            synchronized(lock) {
                if (instance == null) {
                    instance = BitmapCache()
                }

                return instance as BitmapCache
            }
        }
    }
}