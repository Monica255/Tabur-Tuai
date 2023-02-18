package com.taburtuaigroup.taburtuai.core.data.source.remote.model

data class WeatherEntity(
    var type:String="",
    var h:String="",
    var datetime:String="",
    var value:String="",
    var celcius:String="",
    var fahrenheit:String="",
    var code:String="",
    var name:String="",
    var card:String="",
    var kt:String="",
    var mph:String="",
    var kph:String="",
    var ms:String=""
)

data class Param(
    val id:String="",
    val description:String="",
    val type:String="",
    val times:List<WeatherEntity>
)

data class WeatherData(
    val id:String="",
    val description:String="",
    val domain:String="",
    var params:List<Param>
)

data class ResponseWeather(
    val success:Boolean,
    val message:String?,
    var data:WeatherData
)