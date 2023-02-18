package com.taburtuaigroup.taburtuai.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFireStore():FirebaseFirestore=FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage():FirebaseStorage=FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth():FirebaseAuth=FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDb():FirebaseDatabase=FirebaseDatabase.getInstance(FIREBASE_URL).also { it.setPersistenceEnabled(true) }

    companion object{
        private const val FIREBASE_URL =
            "https://smart-farming-andro-default-rtdb.asia-southeast1.firebasedatabase.app/"
    }
}