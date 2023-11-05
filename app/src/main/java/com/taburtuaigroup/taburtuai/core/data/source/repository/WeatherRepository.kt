package com.taburtuaigroup.taburtuai.core.data.source.repository

import com.taburtuaigroup.taburtuai.core.util.AppExecutors
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.data.source.remote.network.RemoteDataSource
import com.taburtuaigroup.taburtuai.core.domain.repository.IWeatherRepository
import com.taburtuaigroup.taburtuai.core.domain.model.WeatherForecast
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository@Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val appExecutors: AppExecutors
):IWeatherRepository {
    //private lateinit var savedData:ResponseWeather
    override suspend fun getWeatherForecast(
        province: String,
        city: String
    ): Flow<Resource<WeatherForecast>> = remoteDataSource.getWeatherForecast(province, city)
    /*override fun getWeatherForecast(
        province: String,
        city: String
    ): Flow<Resource<WeatherForcast>> =object : NetworkBoundResource<WeatherForcast,ResponseWeather>(),
        Flow<Resource<WeatherForcast>> {
        override fun query(): Flow<WeatherForcast> {
            val resultData=DataMapper.responseWeatherToWeatherForecast(savedData)
            return flow { resultData?.let { emit(it) } }
        }

        @InternalCoroutinesApi
        override suspend fun collect(collector: FlowCollector<Resource<WeatherForcast>>) {

        }

        override suspend fun fetch(): Flow<ApiResponse<ResponseWeather>> {
            return remoteDataSource.getWeatherForecast(province, city)
        }

        override suspend fun saveFetchResult(data: ResponseWeather) {
            savedData=data
        }

    }.asFlow()*/
}