package com.example.taburtuai.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taburtuai.data.Repository
import com.example.taburtuai.data.UserData

class ProfileViewModel(private val repository: Repository) :ViewModel() {

    fun setSelaluLoginPetani(isAlwaysLogin:Boolean)=repository.setSelaluLoginPetani(isAlwaysLogin)

    fun signOut()=repository.signOut()

    val userData=repository.getUserData()

    val firebaseUser=repository.firebaseUser

    val isConnected=repository.isConnected

    var newData=MutableLiveData<UserData>()

    val isLoading=repository.isLoading

    val message=repository.message

    fun updateUserData(data:UserData)=repository.updateUserData(data)

    init {
        newData.value=UserData()
    }
}