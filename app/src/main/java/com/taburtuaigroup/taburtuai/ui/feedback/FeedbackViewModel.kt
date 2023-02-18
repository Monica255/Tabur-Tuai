package com.taburtuaigroup.taburtuai.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.taburtuaigroup.taburtuai.core.domain.usecase.FeedBackUseCase
import com.taburtuaigroup.taburtuai.core.domain.model.Masukan
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(private val feedBackUseCase: FeedBackUseCase) : ViewModel() {
    suspend fun kirimMasukan(data: Masukan)=feedBackUseCase.kirimMasukan(data).asLiveData()

    val isCanSendFeedBack=feedBackUseCase.isCanSendFeedBack()

}