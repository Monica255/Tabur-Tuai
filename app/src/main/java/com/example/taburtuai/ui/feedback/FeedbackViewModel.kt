package com.example.taburtuai.ui.feedback

import androidx.lifecycle.ViewModel
import com.example.taburtuai.data.Masukan
import com.example.taburtuai.data.Repository

class FeedbackViewModel(private val repository: Repository) : ViewModel() {
    val message=repository.message
    val isLoading=repository.isLoading

    fun kirimMasukan(data:Masukan)=repository.kirimMasukan(data)

    val isCanSendFeedBack=repository.isCanSendFeedBack()

    val isConnected=repository.isConnected
}