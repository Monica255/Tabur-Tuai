package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.data.Repository
import com.taburtuaigroup.taburtuai.util.KategoriArtikel

class ArtikelViewModel(private val repository: Repository) : ViewModel() {
    val listArtikelTerpopuler = repository.listArtikelTerpopuler

    fun favoriteArtikel(artikel: Artikel) = repository.favoriteArtikel(artikel)

    val listArtikel = repository.listArtikel

    val isLoading=repository.isLoading

    var currentDes = ""
    var mKategoriArtikel = KategoriArtikel.SEMUA
        set(value) {
            field = value
        }
    var mKeyword = ""

    fun setKategoriArtikel(kategoriArtikel: KategoriArtikel) {
            mKategoriArtikel = kategoriArtikel
            repository.getListArtikelByKategori(kategoriArtikel)
    }

    val pagingData = MutableLiveData<LiveData<PagingData<Artikel>>>()

    fun getData(kategoriArtikel: KategoriArtikel = mKategoriArtikel, keyword: String = mKeyword) {
        if (mKategoriArtikel != kategoriArtikel) mKategoriArtikel = kategoriArtikel
        if (mKeyword != keyword) mKeyword = keyword

        pagingData.value =
            repository.getPagingArtikelByKategori(kategoriArtikel, keyword).cachedIn(viewModelScope)

    }

    fun getArtikelTerpopuler() = repository.getArtikelTerpopuler()

}