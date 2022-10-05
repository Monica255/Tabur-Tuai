package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.data.Repository
import com.taburtuaigroup.taburtuai.util.KategoriArtikel

class PenyakitTumbuhanViewModel(private val repository: Repository) :ViewModel() {
    val listPenyakitTerpopuler = repository.listPenyakitTerpopuler

    fun favoritePenyakit(penyakit: PenyakitTumbuhan) = repository.favoritePenyakit(penyakit)

    val listPenyakit = repository.listPenyakit

    val isLoading=repository.isLoading

    var currentDes = ""
    var mKeyword = ""

    fun getListPenyakit() {
        repository.getListPenyakit()
    }

    val pagingData = MutableLiveData<LiveData<PagingData<PenyakitTumbuhan>>>()

    fun getData( keyword: String = mKeyword) {
        if (mKeyword != keyword) mKeyword = keyword
        pagingData.value =
            repository.getPagingPenyakit(keyword).cachedIn(viewModelScope)

    }

    fun getPenyakitTerpopuler() = repository.getPenyakitTerpopuler()
}