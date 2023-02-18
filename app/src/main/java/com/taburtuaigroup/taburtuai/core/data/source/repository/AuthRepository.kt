package com.taburtuaigroup.taburtuai.core.data.source.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.data.source.remote.firebase.FirebaseDataSource
import com.taburtuaigroup.taburtuai.core.domain.repository.IAuthRepository
import com.taburtuaigroup.taburtuai.core.domain.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : IAuthRepository {
    override fun getCurrentUser(): FirebaseUser? = firebaseDataSource.currentUser

    override suspend fun registerAccount(
        email: String,
        pass: String,
        name: String,
        telepon: String
    ): Flow<Resource<String>> = firebaseDataSource.registerAccount(email, pass, name, telepon)


    override suspend fun firebaseAuthWithGoogle(
        idToken: String
    ): Flow<Resource<String>> = firebaseDataSource.firebaseAuthWithGoogle(idToken)

    override suspend fun login(email: String, pass: String): Flow<Resource<String>> = firebaseDataSource.login(email, pass)

    override fun getUserData(): MutableLiveData<UserData?> =firebaseDataSource.getUserData()

    override fun signOut() {
        firebaseDataSource.signOut()
    }

    override suspend fun uploadProfilePicture(filePath: Uri): Flow<Resource<String>> = firebaseDataSource.uploadProfilePicture(filePath)

    override suspend fun updateUserData(data: UserData): Flow<Resource<String>> = firebaseDataSource.updateUserData(data)

}