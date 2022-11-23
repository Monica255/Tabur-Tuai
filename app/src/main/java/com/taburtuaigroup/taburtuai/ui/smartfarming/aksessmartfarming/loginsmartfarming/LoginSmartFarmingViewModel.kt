package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.loginsmartfarming

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.Repository

class LoginSmartFarmingViewModel(private val repository: Repository) :ViewModel() {

    fun loginPetani(idPetani:String,passPetani:String)=repository.loginPetani(idPetani,passPetani)

    val petani=repository.petani

    val isLoading=repository.isLoading

    val message=repository.message

}