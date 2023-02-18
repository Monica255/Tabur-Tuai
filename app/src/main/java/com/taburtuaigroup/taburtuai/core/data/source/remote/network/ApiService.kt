package com.taburtuaigroup.taburtuai.core.data.source.remote.network

import com.taburtuaigroup.taburtuai.core.data.source.remote.model.ResponseWeather
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("{provinsi}/{kota}")
    suspend fun getWeatherForcast(
        @Path("provinsi") provinsi:String,
        @Path("kota") kota:String
    ): ResponseWeather
}