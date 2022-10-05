package com.taburtuaigroup.taburtuai.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.Repository
import com.taburtuaigroup.taburtuai.data.UserData
import com.taburtuaigroup.taburtuai.util.Event

class EditProfileViewModel(private val repository: Repository) :ViewModel() {
    fun uploadProfilePic(uri: Uri)=repository.uploadProfilePicture(uri)

    val userData=repository.getUserData()

    val isConnected=repository.isConnected

    var newData= MutableLiveData<UserData>()

    val isLoading=repository.isLoading

    val message=repository.message

    var isDialogOpen=false

    fun updateUserData(data: UserData)=repository.updateUserData(data)

    init {
        newData.value= UserData()
    }
}