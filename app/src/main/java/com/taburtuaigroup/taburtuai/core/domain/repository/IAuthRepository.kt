package com.taburtuaigroup.taburtuai.core.domain.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun getCurrentUser():FirebaseUser?
    suspend fun registerAccount(
        email: String,
        pass: String,
        name: String,
        telepon: String
    ): Flow<Resource<String>>

    suspend fun firebaseAuthWithGoogle(idToken: String): Flow<Resource<String>>

    suspend fun login(email: String, pass: String): Flow<Resource<String>>

    fun getUserData(uid:String?): MutableLiveData<UserData?>

    fun signOut()

    suspend fun uploadProfilePicture(filePath: Uri): Flow<Resource<String>>

    suspend fun updateUserData(data: UserData):Flow<Resource<String>>

}