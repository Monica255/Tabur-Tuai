package com.taburtuaigroup.taburtuai.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.Repository
import com.taburtuaigroup.taburtuai.data.UserData

class ProfileViewModel(private val repository: Repository) :ViewModel() {

    fun setSelaluLoginPetani(isAlwaysLogin:Boolean)=repository.setSelaluLoginPetani(isAlwaysLogin)

    fun signOut()=repository.signOut()

    val firebaseUser=repository.firebaseUser

    val userData=repository.getUserData()
}