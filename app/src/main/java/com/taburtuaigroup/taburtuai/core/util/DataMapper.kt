package com.taburtuaigroup.taburtuai.core.util

import android.util.Log
import com.taburtuaigroup.taburtuai.core.data.source.remote.model.ResponseWeather
import com.taburtuaigroup.taburtuai.core.data.source.remote.model.WeatherEntity
import com.taburtuaigroup.taburtuai.core.domain.model.DailyWeather
import com.taburtuaigroup.taburtuai.core.domain.model.WeatherForecast
import com.taburtuaigroup.taburtuai.core.domain.model.WeatherTime

class DataMapper {
    companion object {
        fun responseWeatherToWeatherForecast(data: ResponseWeather): WeatherForecast? {
            var returnData: WeatherForecast?
            try {
                var x=0
                val kota = data.data.description
                val provinsi = data.data.domain
                val dailyWeather = mutableListOf<DailyWeather>()

                var listHumidity = listOf<WeatherEntity>()
                var listTemperature = listOf<WeatherEntity>()
                var listWd = listOf<WeatherEntity>()
                var listWs = listOf<WeatherEntity>()
                var listWeather = listOf<WeatherEntity>()

                for (i in data.data.params) {
                    when (i.id) {
                        "hu" -> listHumidity = i.times
                        "t" -> listTemperature = i.times
                        "wd" -> listWd = i.times
                        "ws" -> listWs = i.times
                        "weather" -> listWeather = i.times
                    }
                }
                for (i in 1..3) {
                    val daily = DailyWeather(
                        date = DateConverter.convertStringToData(listHumidity[x].datetime, false),
                        diniHari = WeatherTime(
                            listHumidity[0 + x].value,
                            listTemperature[0 + x].celcius,
                            listWs[0 + x].kph,
                            listWd[0 + x].card,
                            listWeather[0 + x].code.toInt(),
                            listWeather[0 + x].name
                        ),
                        pagiHari = WeatherTime(
                            listHumidity[1 + x].value,
                            listTemperature[1 + x].celcius,
                            listWs[1 + x].kph,
                            listWd[1 + x].card,
                            listWeather[1 + x].code.toInt(),
                            listWeather[1 + x].name
                        ),
                        siangHari = WeatherTime(
                            listHumidity[2 + x].value,
                            listTemperature[2 + x].celcius,
                            listWs[2 + x].kph,
                            listWd[2 + x].card,
                            listWeather[2 + x].code.toInt(),
                            listWeather[2 + x].name
                        ),
                        malamHari = WeatherTime(
                            listHumidity[3 + x].value,
                            listTemperature[3 + x].celcius,
                            listWs[3 + x].kph,
                            listWd[3 + x].card,
                            listWeather[3 + x].code.toInt(),
                            listWeather[3 + x].name
                        ),
                    )
                    dailyWeather.add(daily)
                    x += 4
                }
                returnData = WeatherForecast(kota, provinsi, dailyWeather)
            } catch (e: Exception) {
                returnData = null
                Log.d("TAG", e.message.toString())
            }
            return returnData
        }
    }
}