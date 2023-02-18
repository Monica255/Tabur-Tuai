package com.taburtuaigroup.taburtuai.ui.profile

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.core.domain.usecase.AuthUseCase
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val authUseCase: AuthUseCase,private val smartFarmingUseCase: SmartFarmingUseCase) :ViewModel() {

    fun setSelaluLoginPetani(isAlwaysLogin:Boolean)=smartFarmingUseCase.setSelaluLoginPetani(isAlwaysLogin)

    fun signOut()=authUseCase.signOut()

    val userData=authUseCase.getUserData()
}