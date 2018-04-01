package com.tinashe.weather

import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

/**
 * Mockito Utils
 * Use `whenever` instead of `when` since its reserved in kotlin
 */
inline fun <reified T> mock() = Mockito.mock(T::class.java)
inline fun <T> whenever(methodCall: T) : OngoingStubbing<T> =
        Mockito.`when`(methodCall)
