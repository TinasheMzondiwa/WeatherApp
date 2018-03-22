package com.tinashe.weather.ui.base

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by tinashe on 2018/02/04.
 */
abstract class RxAwareViewModel : ViewModel() {

    val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        unSubscribe()
    }

    abstract fun subscribe()

    open fun unSubscribe() {
        disposables.clear()
    }
}