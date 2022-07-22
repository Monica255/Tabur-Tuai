package com.example.taburtuai

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taburtuai.data.Repository
import com.example.taburtuai.ui.home.HomeViewModel

import com.example.taburtuai.ui.loginsignup.LoginSignupViewModel
import com.example.taburtuai.ui.smartfarming.kebun.KebunViewModel
import com.example.taburtuai.ui.smartfarming.loginsmartfarming.LoginSmartFarmingViewModel
import com.example.taburtuai.ui.smartfarming.pilihkebun.PilihKebunViewModel

class ViewModelFactory private constructor(private val context: Context) :
    ViewModelProvider.Factory{

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(activity: Activity): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(activity)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(LoginSignupViewModel::class.java) -> {
                LoginSignupViewModel(Repository.getInstance(context)) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(Repository.getInstance(context)) as T
            }
            modelClass.isAssignableFrom(LoginSmartFarmingViewModel::class.java) -> {
                LoginSmartFarmingViewModel(Repository.getInstance(context)) as T
            }
            modelClass.isAssignableFrom(PilihKebunViewModel::class.java) -> {
                PilihKebunViewModel(Repository.getInstance(context)) as T
            }
            modelClass.isAssignableFrom(KebunViewModel::class.java) -> {
                KebunViewModel(Repository.getInstance(context)) as T
            }

            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
}