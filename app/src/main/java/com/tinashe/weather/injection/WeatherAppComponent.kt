package com.tinashe.weather.injection

import com.tinashe.weather.WeatherApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * Created by tinashe on 2018/03/20.
 */
@Singleton
@Component(modules = [(WeatherAppModule::class),
    (AndroidInjectionModule::class),
    (ViewModelBuilder::class),
    (ActivityBuilder::class)])
interface WeatherAppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: WeatherApp): Builder

        fun build(): WeatherAppComponent
    }

    fun inject(app: WeatherApp)
}