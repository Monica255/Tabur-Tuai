package com.taburtuaigroup.taburtuai

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taburtuaigroup.taburtuai.data.Repository
import com.taburtuaigroup.taburtuai.ui.feedback.FeedbackViewModel
import com.taburtuaigroup.taburtuai.ui.home.HomeViewModel
import com.taburtuaigroup.taburtuai.ui.listpetanikebun.PetaniKebunViewModel

import com.taburtuaigroup.taburtuai.ui.loginsignup.LoginSignupViewModel
import com.taburtuaigroup.taburtuai.ui.profile.EditProfileViewModel
import com.taburtuaigroup.taburtuai.ui.profile.ProfileViewModel
import com.taburtuaigroup.taburtuai.ui.smartfarming.SmartFarmingViewModel
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun.KebunViewModel
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.loginsmartfarming.LoginSmartFarmingViewModel
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.pilihkebun.PilihKebunViewModel
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.ArtikelViewModel
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.DetailArtikelViewModel
import com.taburtuaigroup.taburtuai.ui.smartfarming.fav.FavViewModel
import com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan.DetailPenyakitViewModel
import com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan.PenyakitTumbuhanViewModel

class ViewModelFactory private constructor(private val application: Application) :
    ViewModelProvider.Factory{

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(activity: Application): ViewModelFactory {
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
                LoginSignupViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(LoginSmartFarmingViewModel::class.java) -> {
                LoginSmartFarmingViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(PilihKebunViewModel::class.java) -> {
                PilihKebunViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(KebunViewModel::class.java) -> {
                KebunViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(PetaniKebunViewModel::class.java) -> {
                PetaniKebunViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(FeedbackViewModel::class.java) -> {
                FeedbackViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(SmartFarmingViewModel::class.java) -> {
                SmartFarmingViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(DetailArtikelViewModel::class.java) -> {
                DetailArtikelViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(DetailPenyakitViewModel::class.java) -> {
                DetailPenyakitViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(FavViewModel::class.java) -> {
                FavViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(ArtikelViewModel::class.java) -> {
                ArtikelViewModel(Repository.getInstance(application)) as T
            }
            modelClass.isAssignableFrom(PenyakitTumbuhanViewModel::class.java) -> {
                PenyakitTumbuhanViewModel(Repository.getInstance(application)) as T
            }

            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
}