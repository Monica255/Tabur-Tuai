package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.pilihkebun

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.Repository

class PilihKebunViewModel(private val repository: Repository) : ViewModel() {

    val petani=repository.petani

    fun getAllKebun(idPetani: String, kebunName:String="")=repository.getAllKebunPetani(idPetani,kebunName)

    val kebunPetani=repository.kebunPetani

    val isConnected=repository.isConnected

    fun logoutPetani()=repository.logoutPetani()

}