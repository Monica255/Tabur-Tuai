package com.taburtuaigroup.taburtuai.ui.listpetanikebun

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.Repository

class PetaniKebunViewModel(private val repository: Repository) : ViewModel() {

    init {
        repository.getAllPetani()
        repository.getAllKebun()
    }

    fun getAllPetani(filter:String)=repository.getAllPetani(filter)

    fun getAllKebun(filter:String)=repository.getAllKebun(filter)

    val allPetani=repository.allPetani

    val allKebun=repository.allKebun

    val isConnected=repository.isConnected

}