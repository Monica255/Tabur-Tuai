package com.taburtuaigroup.taburtuai.ui.splash

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.core.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(useCase: AuthUseCase):ViewModel(){
    val currentUser=useCase.getCurrentUser()
}