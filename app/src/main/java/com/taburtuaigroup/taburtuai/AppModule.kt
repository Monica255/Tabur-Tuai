package com.taburtuaigroup.taburtuai

import com.taburtuaigroup.taburtuai.core.data.source.repository.*
import com.taburtuaigroup.taburtuai.core.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherUseCase(weatherRepository: WeatherRepository): WeatherUseCase=WeatherInteractor(weatherRepository)

    @Provides
    @Singleton
    fun provideAuthUseCase(authRepository: AuthRepository): AuthUseCase=AuthInteractor(authRepository)

    @Provides
    @Singleton
    fun provideSmartFarmingUseCase(smartFarmingRepository: SmartFarmingRepository):SmartFarmingUseCase =SmartFarmingInteractor(smartFarmingRepository)

    @Provides
    @Singleton
    fun provideFeedbackUseCase(feedBackRepository: FeedBackRepository):FeedBackUseCase =FeedBackInteractor(feedBackRepository)

    @Provides
    @Singleton
    fun provideForumUseCase(forumRepository: ForumRepository):ForumUseCase =ForumInteractor(forumRepository)

}