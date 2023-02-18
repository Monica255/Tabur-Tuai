package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import com.taburtuaigroup.taburtuai.core.domain.usecase.WeatherUseCase
import com.taburtuaigroup.taburtuai.core.domain.model.DailyWeather
import com.taburtuaigroup.taburtuai.core.domain.model.Device
import com.taburtuaigroup.taburtuai.core.domain.model.MonitoringKebun
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class KebunViewModel @Inject constructor(
    private val weatherUseCase: WeatherUseCase,
    private val smartFarmingUseCase: SmartFarmingUseCase
    ) :ViewModel() {

    var kebunId:String=""

    fun getKebun(idKebun:String)=smartFarmingUseCase.getKebun(idKebun)

    fun getMonitoringKebun(idKebun: String)=smartFarmingUseCase.getMonitoringKebun(idKebun)

    fun getControllingKebun(idKebun: String)=smartFarmingUseCase.getControllingKebun(idKebun)

    val monitoring=MutableLiveData<MonitoringKebun>()

    val isConnected=smartFarmingUseCase.isConnected

    val controlling=MutableLiveData<List<Device>>()

    fun updateDeviceState(idKebun:String,idDevice:String,value:Int)=smartFarmingUseCase.updateDeviceState(idKebun,idDevice,value)

    val weatherForcastData= MutableLiveData<List<DailyWeather>>()

    suspend fun getWeatherForecast(province: String,city: String)=weatherUseCase.getWeatherForecast(province,city).asLiveData()
}