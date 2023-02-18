package com.taburtuaigroup.taburtuai.core.data.source.remote.network

import com.taburtuaigroup.taburtuai.core.domain.model.WeatherForcast
import com.taburtuaigroup.taburtuai.core.util.DataMapper
import com.taburtuaigroup.taburtuai.core.data.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun getWeatherForecast(province: String, city: String): Flow<Resource<WeatherForcast>> {
        //get data from remote api
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.getWeatherForcast(province,city)
                val success = response.success
                val weatherForecast= DataMapper.responseWeatherToWeatherForecast(response)
                if (success){
                    if(weatherForecast!=null){
                        emit(Resource.Success(weatherForecast))
                    }else{
                        emit(Resource.Error("Error"))
                    }

                } else {
                    emit(Resource.Error("Error"))
                }
            } catch (e : Exception){
                emit(Resource.Error(e.message.toString()))
                //Log.e("RemoteDataSource", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }
}