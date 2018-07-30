package com.tinashe.weather.injection

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.tinashe.weather.ui.about.AboutInfoViewModel
import com.tinashe.weather.ui.home.HomeViewModel
import com.tinashe.weather.ui.home.detail.DetailViewModel
import com.tinashe.weather.ui.home.place.PlaceForecastViewModel
import com.tinashe.weather.ui.splash.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by tinashe on 2018/03/20.
 */
@Module
internal abstract class ViewModelBuilder {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    internal abstract fun bindSplashViewModel(splashViewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    internal abstract fun bindDetailViewModel(detailViewModel: DetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutInfoViewModel::class)
    internal abstract fun bindAboutInfoViewModel(aboutInfoViewModel: AboutInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlaceForecastViewModel::class)
    internal abstract fun bindPlaceForecastViewModel(placeForecastViewModel: PlaceForecastViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFractory(factory: ViewModelFactory): ViewModelProvider.Factory
}