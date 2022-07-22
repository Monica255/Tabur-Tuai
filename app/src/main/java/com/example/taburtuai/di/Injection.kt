package com.example.taburtuai.di

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.taburtuai.data.Repository
import com.google.firebase.database.FirebaseDatabase


object Injection {
    fun provideRepository(context: Context): Repository {
        var mDb: FirebaseDatabase? = null
        mDb =
            FirebaseDatabase.getInstance(Repository.FIREBASE_URL)
        mDb!!.setPersistenceEnabled(true)
        return Repository(context,mDb)
    }
}