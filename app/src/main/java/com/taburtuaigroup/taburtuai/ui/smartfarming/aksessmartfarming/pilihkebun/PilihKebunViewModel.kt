package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.pilihkebun

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import com.taburtuaigroup.taburtuai.core.domain.model.Petani
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PilihKebunViewModel @Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase) : ViewModel() {

    var petani: Petani?=null

    fun getAllKebun(idPetani: String, kebunName:String="")=smartFarmingUseCase.getAllKebunPetani(idPetani,kebunName)

}