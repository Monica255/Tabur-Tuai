package com.taburtuaigroup.taburtuai.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.taburtuaigroup.taburtuai.core.domain.usecase.AuthUseCase
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(authUseCase: AuthUseCase,private val smartFarmingUseCase: SmartFarmingUseCase) :ViewModel() {
    val userData=authUseCase.getUserData(null)

    val isConnected=smartFarmingUseCase.isConnected

    fun getControllingKebun(idKebun: String)=smartFarmingUseCase.getControllingKebun(idKebun)

    fun updateDeviceState(idKebun:String,idDevice:String,state:Int)=smartFarmingUseCase.updateDeviceState(idKebun,idDevice,state).asLiveData()
}