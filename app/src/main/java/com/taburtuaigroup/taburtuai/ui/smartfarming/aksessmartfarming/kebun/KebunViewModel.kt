package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.Repository

class KebunViewModel(private val repository: Repository) :ViewModel() {

    var kebunId:String=""
        set(value) {
            field=value
            repository.idActiveKebun=field
            getKebun(value)
            getRealtimeKebun(value)
        }

    fun clearDataKebun()=repository.clearDataKebun()

    private fun getKebun(idKebun:String)=repository.getKebun(idKebun)

    val kebun=repository.kebun

    private fun getRealtimeKebun(idKebun: String)=repository.getRealtimeKebun(idKebun)

    val monitoring=repository.monitoring

    val isConnected=repository.isConnected

    val controlling=repository.controlling

    val weatherForcast=repository.weatherForcast

    val message=repository.message

    fun updateDeviceState(idKebun:String,idDevice:String,value:Int)=repository.updateDeviceState(idKebun,idDevice,value)

    fun getWeatherForcast(province:String,city:String){
        repository.getWeatherForcast(province,city)
    }
}