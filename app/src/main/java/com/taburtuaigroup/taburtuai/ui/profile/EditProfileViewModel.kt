package com.taburtuaigroup.taburtuai.ui.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.taburtuaigroup.taburtuai.core.domain.usecase.AuthUseCase
import com.taburtuaigroup.taburtuai.core.domain.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(private val authUseCase: AuthUseCase) :ViewModel() {
    suspend fun uploadProfilePic(uri: Uri)=authUseCase.uploadProfilePicture(uri).asLiveData()

    val userData=authUseCase.getUserData()

    val currentUser=authUseCase.getCurrentUser()

    var newData= MutableLiveData<UserData>()

    var isDialogOpen=false

    suspend fun updateUserData(data: UserData)=authUseCase.updateUserData(data).asLiveData()

    init {
        newData.value= UserData()
    }
}