package com.taburtuaigroup.taburtuai.ui.loginsignup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.taburtuaigroup.taburtuai.core.domain.usecase.AuthUseCase
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginSignupViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val smartFarmingUseCase: SmartFarmingUseCase
    ) :ViewModel() {

    var currentUser=authUseCase.getCurrentUser()

    suspend fun login(email:String,pass:String)=authUseCase.login(email,pass).asLiveData()

    suspend fun registerAccount(email:String,pass: String,name:String,telepon:String)=authUseCase.registerAccount(email, pass, name, telepon).asLiveData()

    suspend fun firebaseAuthWithGoogle(idToken:String)=authUseCase.firebaseAuthWithGoogle(idToken).asLiveData()

    fun getAllActiveScheduler() =smartFarmingUseCase.getScheduler("",true)

}