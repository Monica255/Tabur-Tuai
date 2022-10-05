package com.taburtuaigroup.taburtuai.data.retrofit

import com.taburtuaigroup.taburtuai.data.ResponseWeather
import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("{provinsi}/{kota}")
    fun getWeatherForcast(
        @Path("provinsi") provinsi:String,
        @Path("kota") kota:String
    ): Call<ResponseWeather>
}