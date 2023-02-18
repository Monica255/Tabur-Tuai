package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import com.taburtuaigroup.taburtuai.core.domain.model.PenyakitTumbuhan
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PenyakitTumbuhanViewModel @Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase) :ViewModel() {
    var currentDes = ""
    var mKeyword = ""

    fun getListPenyakit() = smartFarmingUseCase.getListPenyakit().asLiveData()

    val pagingData = MutableLiveData<LiveData<PagingData<PenyakitTumbuhan>>>()

    fun getData( keyword: String = mKeyword) {
        if (mKeyword != keyword) mKeyword = keyword
        pagingData.value =
            smartFarmingUseCase.getPagingPenyakit(keyword).cachedIn(viewModelScope)

    }

    fun getPenyakitTerpopuler() = smartFarmingUseCase.getPenyakitTerpopuler().asLiveData()
}