package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.penjadwalan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.taburtuaigroup.taburtuai.core.domain.model.Mscheduler
import com.taburtuaigroup.taburtuai.core.domain.model.Petani
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SchedulerViewModel @Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase):ViewModel() {
    fun getScheduler(idPetani:String,onlyActive:Boolean)=smartFarmingUseCase.getScheduler(idPetani, onlyActive)

    fun updateSchedule(mSchedule:Mscheduler,status:Boolean,context: Context)= smartFarmingUseCase.updateScheduleData(mSchedule,status,context).asLiveData()

    fun deleteScheduler(mSchedule: Mscheduler) =smartFarmingUseCase.deleteScheduler(mSchedule).asLiveData()

    var petani:Petani?=null
}