package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import com.taburtuaigroup.taburtuai.core.domain.model.Artikel
import com.taburtuaigroup.taburtuai.core.util.KategoriArtikel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtikelViewModel @Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase) : ViewModel() {
    var currentDes = ""
    var mKategoriArtikel = KategoriArtikel.SEMUA
    var mKeyword = ""

    fun getListArtikelByKategori(kategoriArtikel: KategoriArtikel): LiveData<Resource<List<Artikel>>> {
        mKategoriArtikel=kategoriArtikel
        return smartFarmingUseCase.getListArtikelByKategori(kategoriArtikel).asLiveData()
    }

    fun getArtikelTerpopuler() = smartFarmingUseCase.getArtikelTerpopuler().asLiveData()


    //Paging
    val pagingData = MutableLiveData<LiveData<PagingData<Artikel>>>()

    fun getData(kategoriArtikel: KategoriArtikel = mKategoriArtikel, keyword: String = mKeyword) {
        if (mKategoriArtikel != kategoriArtikel) mKategoriArtikel = kategoriArtikel
        if (mKeyword != keyword) mKeyword = keyword

        pagingData.value =
            smartFarmingUseCase.getPagingArtikelByKategori(kategoriArtikel, keyword).cachedIn(viewModelScope)
    }
}