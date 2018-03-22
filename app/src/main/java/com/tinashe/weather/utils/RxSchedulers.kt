package com.tinashe.weather.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by tinashe on 2018/02/01.
 */
data class RxSchedulers(
        val database: Scheduler = Schedulers.single(),
        val network: Scheduler = Schedulers.io(),
        val main: Scheduler = AndroidSchedulers.mainThread())