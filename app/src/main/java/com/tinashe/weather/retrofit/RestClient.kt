package com.tinashe.weather.retrofit

import com.tinashe.weather.BuildConfig
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by tinashe on 2018/02/01.
 */
object RestClient {

    private const val TIMEOUT_SECONDS = 60L

    private val client: OkHttpClient
        get() {
            val clientBuilder = OkHttpClient.Builder()
            clientBuilder.readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            clientBuilder.connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)

            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                clientBuilder.addInterceptor(interceptor)
            }

            return clientBuilder.build()
        }

    private val retrofit: Retrofit
        get() {
            return Retrofit.Builder()
                    .baseUrl(BuildConfig.URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .client(client)
                    .build()
        }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }
}