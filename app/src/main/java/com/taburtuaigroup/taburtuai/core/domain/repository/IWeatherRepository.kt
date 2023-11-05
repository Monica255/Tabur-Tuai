package com.taburtuaigroup.taburtuai.core.domain.repository

import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.WeatherForecast
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository{
    suspend fun getWeatherForecast(province: String, city: String): Flow<Resource<WeatherForecast>>
}