package com.taburtuaigroup.taburtuai.core.domain.usecase

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.repository.IAuthRepository
import com.taburtuaigroup.taburtuai.core.domain.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthInteractor@Inject constructor(private val repo: IAuthRepository):AuthUseCase {
    override fun getCurrentUser(): FirebaseUser?=repo.getCurrentUser()

    override suspend fun registerAccount(
        email: String,
        pass: String,
        name: String,
        telepon: String
    )= repo.registerAccount(email,pass,name,telepon)

    override suspend fun firebaseAuthWithGoogle(idToken: String)=repo.firebaseAuthWithGoogle(idToken)

    override suspend fun login(email: String, pass: String): Flow<Resource<String>> = repo.login(email, pass)

    override fun getUserData(): MutableLiveData<UserData?> = repo.getUserData()

    override fun signOut() {
        repo.signOut()
    }

    override suspend fun uploadProfilePicture(filePath: Uri): Flow<Resource<String>> =repo.uploadProfilePicture(filePath)
    override suspend fun updateUserData(data: UserData): Flow<Resource<String>> = repo.updateUserData(data)
}