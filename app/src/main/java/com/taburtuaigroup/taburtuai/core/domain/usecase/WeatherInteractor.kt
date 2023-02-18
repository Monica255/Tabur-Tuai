package com.taburtuaigroup.taburtuai.core.domain.usecase

import com.taburtuaigroup.taburtuai.core.domain.repository.IWeatherRepository
import javax.inject.Inject

class WeatherInteractor @Inject constructor(private val repo: IWeatherRepository) : WeatherUseCase {
    override suspend fun getWeatherForecast(
        province: String,
        city: String
    ) = repo.getWeatherForecast(province, city)
}