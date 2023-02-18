package com.taburtuaigroup.taburtuai.core.di

import com.taburtuaigroup.taburtuai.core.data.source.repository.AuthRepository
import com.taburtuaigroup.taburtuai.core.data.source.repository.FeedBackRepository
import com.taburtuaigroup.taburtuai.core.data.source.repository.SmartFarmingRepository
import com.taburtuaigroup.taburtuai.core.data.source.repository.WeatherRepository
import com.taburtuaigroup.taburtuai.core.domain.repository.IAuthRepository
import com.taburtuaigroup.taburtuai.core.domain.repository.IFeedBackRepository
import com.taburtuaigroup.taburtuai.core.domain.repository.ISmartFarmingRepository
import com.taburtuaigroup.taburtuai.core.domain.repository.IWeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [NetworkModule::class,FirebaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideWeatherRepository(weatherRepository: WeatherRepository): IWeatherRepository

    @Binds
    abstract fun provideAuthRepository(authRepository: AuthRepository): IAuthRepository

    @Binds
    abstract fun provideSmartFarmingRepository(smartFarmingRepository: SmartFarmingRepository): ISmartFarmingRepository

    @Binds
    abstract fun provideFeedBackRepository(feedBackRepository: FeedBackRepository): IFeedBackRepository

}