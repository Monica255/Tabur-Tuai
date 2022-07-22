package com.example.taburtuai.ui.smartfarming.kebun

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taburtuai.data.Device
import com.example.taburtuai.data.Repository

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

    fun updateDeviceState(idKebun:String,idDevice:String,value:Int)=repository.updateDeviceState(idKebun,idDevice,value)

}