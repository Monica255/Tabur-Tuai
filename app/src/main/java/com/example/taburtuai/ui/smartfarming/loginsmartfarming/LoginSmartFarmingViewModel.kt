package com.example.taburtuai.ui.smartfarming.loginsmartfarming

import androidx.lifecycle.ViewModel
import com.example.taburtuai.data.Repository

class LoginSmartFarmingViewModel(private val repository: Repository) :ViewModel() {

    fun loginPetani(idPetani:String,passPetani:String)=repository.loginPetani(idPetani,passPetani)

    val idPetani=repository.idPetani

    val isLoading=repository.isLoading

    val message=repository.message

    val isConnected=repository.isConnected

}