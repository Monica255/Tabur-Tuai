package com.example.taburtuai.ui.smartfarming.pilihkebun

import androidx.lifecycle.ViewModel
import com.example.taburtuai.data.Repository

class PilihKebunViewModel(private val repository: Repository) : ViewModel() {

    //val idPetani=repository.idPetani

    val petani=repository.petani

    //fun getPetani(idPetani:String)=repository.getPetani(idPetani)

    fun getAllKebun(idPetani: String, kebunName:String="")=repository.getAllKebunPetani(idPetani,kebunName)

    val kebunPetani=repository.kebunPetani

    val isConnected=repository.isConnected

    fun logoutPetani()=repository.logoutPetani()

}