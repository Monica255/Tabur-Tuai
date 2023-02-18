package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import com.taburtuaigroup.taburtuai.core.domain.model.PenyakitTumbuhan
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailPenyakitViewModel @Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase) :ViewModel() {

    fun favoritePenyakit(penyakit: PenyakitTumbuhan)=smartFarmingUseCase.favoritePenyakit(penyakit).asLiveData()

    fun getPenyakit(penyakitId: String)=smartFarmingUseCase.getPenyakit(penyakitId).asLiveData()
}