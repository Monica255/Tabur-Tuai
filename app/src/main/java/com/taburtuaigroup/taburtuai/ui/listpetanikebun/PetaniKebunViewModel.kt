package com.taburtuaigroup.taburtuai.ui.listpetanikebun

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import com.taburtuaigroup.taburtuai.core.domain.model.Kebun
import com.taburtuaigroup.taburtuai.core.domain.model.Petani
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PetaniKebunViewModel @Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase) : ViewModel() {

    fun getAllPetani(filter:String="")=smartFarmingUseCase.getAllPetani(filter)

    fun getAllKebun(filter:String="")=smartFarmingUseCase.getAllKebun(filter)

    val allPetani = MutableLiveData<Resource<List<Petani>>>()

    val allKebun=MutableLiveData<Resource<List<Kebun>>>()





}