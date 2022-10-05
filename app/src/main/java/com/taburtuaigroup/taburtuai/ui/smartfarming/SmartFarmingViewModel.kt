package com.taburtuaigroup.taburtuai.ui.smartfarming

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.data.Repository
import com.taburtuaigroup.taburtuai.util.KategoriArtikel

class SmartFarmingViewModel(private val provideRepository: Repository) : ViewModel() {
    fun autoLoginPetani(idPetani:String)=provideRepository.autoLoginPetani(idPetani)

    val artikel=provideRepository.listArtikel

    val penyakit=provideRepository.listPenyakit

    val message=provideRepository.message

    val isLoading=provideRepository.isLoading

    fun favoriteArtikel(artikel: Artikel)=provideRepository.favoriteArtikel(artikel)

    fun favoritePenyakit(penyakit: PenyakitTumbuhan)=provideRepository.favoritePenyakit(penyakit)

    fun getData(){
        provideRepository.getListArtikelByKategori(KategoriArtikel.SEMUA)
        provideRepository.getListPenyakit()
    }

    init {
        getData()
    }
}