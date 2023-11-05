package com.taburtuaigroup.taburtuai.core.domain.model

data class WeatherTime(
    var humidity:String="",
    var temperature:String="",
    var windSpeed:String="",
    var windDirection:String="",
    var weatherCode:Int=-1,
    var weatherName:String=""
)

data class DailyWeather(
    var date: Mdate,
    var diniHari: WeatherTime,
    var pagiHari: WeatherTime,
    var siangHari: WeatherTime,
    var malamHari: WeatherTime
)

data class WeatherForecast(
    val city:String="",
    val province:String="",
    val dailyWeather:List<DailyWeather>?=null
)