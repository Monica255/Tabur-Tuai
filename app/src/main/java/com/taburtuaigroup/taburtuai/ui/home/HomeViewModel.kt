package com.taburtuaigroup.taburtuai.ui.home

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.Repository

class HomeViewModel(private val provideRepository: Repository) :ViewModel() {

    val userData=provideRepository.getUserData()
}