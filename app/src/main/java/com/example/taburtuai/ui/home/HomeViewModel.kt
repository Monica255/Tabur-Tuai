package com.example.taburtuai.ui.home

import androidx.lifecycle.ViewModel
import com.example.taburtuai.data.Repository

class HomeViewModel(private val provideRepository: Repository) :ViewModel() {

    val userData=provideRepository.getUserData()

    val isConnected=provideRepository.isConnected

    fun autoLoginPetani(idPetani:String)=provideRepository.autoLoginPetani(idPetani)
}