package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.penjadwalan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Device
import com.taburtuaigroup.taburtuai.core.domain.model.Kebun
import com.taburtuaigroup.taburtuai.core.domain.model.Mscheduler
import com.taburtuaigroup.taburtuai.core.domain.model.Petani
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CreateScheduleViewModel  @Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase):ViewModel(){
    fun getAllKebun(idPetani: String, kebunName:String="")=smartFarmingUseCase.getAllKebunPetani(idPetani,kebunName)

    var petani: Petani?=null

    var device:Device?=null

    var kebun=MutableLiveData<Kebun?>()

    var hour:Int?=null

    var minute:Int?=null

    var action:Int?=null

    fun getAllDevice(idKebun: String)= smartFarmingUseCase.getControllingKebun(idKebun)

    fun createSchedule(mscheduler: Mscheduler)= smartFarmingUseCase.addScheduler(mscheduler).asLiveData()

    val isConnected=smartFarmingUseCase.isConnected
}
