package com.tinashe.weather.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.tinashe.weather.data.di.ViewModelFactory

inline fun <reified T : ViewModel> getViewModel(activity: FragmentActivity, factory: ViewModelFactory): T {
    return ViewModelProviders.of(activity, factory)[T::class.java]
}

inline fun <reified T : ViewModel> getViewModel(fragment: Fragment, factory: ViewModelFactory): T {
    return ViewModelProviders.of(fragment, factory)[T::class.java]
}

fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer {
        it?.let(observer)
    })
}
