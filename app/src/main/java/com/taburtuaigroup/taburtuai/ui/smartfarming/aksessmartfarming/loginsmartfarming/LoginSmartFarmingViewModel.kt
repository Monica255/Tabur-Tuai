package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.loginsmartfarming

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginSmartFarmingViewModel @Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase) :ViewModel() {

    fun loginPetani(idPetani:String,passPetani:String)=smartFarmingUseCase.loginPetani(idPetani,passPetani)

}