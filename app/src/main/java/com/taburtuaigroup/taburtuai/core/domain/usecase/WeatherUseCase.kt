package com.taburtuaigroup.taburtuai.core.domain.usecase

import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.WeatherForecast
import kotlinx.coroutines.flow.Flow

interface WeatherUseCase {
    suspend fun getWeatherForecast(province: String, city: String): Flow<Resource<WeatherForecast>>
}