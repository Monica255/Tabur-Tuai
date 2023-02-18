package com.taburtuaigroup.taburtuai.ui.smartfarming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import com.taburtuaigroup.taburtuai.core.util.KategoriArtikel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartFarmingViewModel@Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase) : ViewModel() {
    fun loginPetani(idPetani:String,passPetani:String?)=smartFarmingUseCase.loginPetani(idPetani,passPetani)

    fun getListPenyakitTumbuhan() = smartFarmingUseCase.getListPenyakit().asLiveData()

    fun getListArtikelByKategory(kategoriArtikel: KategoriArtikel) = smartFarmingUseCase.getListArtikelByKategori(kategoriArtikel).asLiveData()

}