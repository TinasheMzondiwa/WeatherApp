package com.tinashe.weather.utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

class RxBus private constructor() {

    private val bus = PublishSubject.create<Any>()

    fun send(event: Any) {
        if (hasObservers()) { // No point in sending this event if nobody's subscribed.
            Timber.d("Firing RxBus event: %s", event.toString())
            bus.onNext(event)
        }
    }

    fun <T> toObservable(type: Class<T>): Observable<T> {
        return bus.filter(type::isInstance).cast(type)
    }

    fun hasObservers(): Boolean {
        return bus.hasObservers()
    }

    companion object {
        private var instance: RxBus? = null

        private val lock = Any()

        fun getInstance(): RxBus {
            synchronized(lock) {
                if (instance == null) {
                    instance = RxBus()
                }

                return instance as RxBus
            }
        }
    }
}