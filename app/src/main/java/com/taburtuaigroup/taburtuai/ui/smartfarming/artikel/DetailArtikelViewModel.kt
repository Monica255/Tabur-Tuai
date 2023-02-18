package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import com.taburtuaigroup.taburtuai.core.domain.model.Artikel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailArtikelViewModel @Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase) :ViewModel() {

    fun favoriteArtikel(artikel: Artikel)=smartFarmingUseCase.favoriteArtikel(artikel).asLiveData()
    fun getArtikel(artikelId: String)=smartFarmingUseCase.getArtikel(artikelId).asLiveData()
}