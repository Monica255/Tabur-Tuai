package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.data.Repository

class DetailPenyakitViewModel(private val repository: Repository) :ViewModel() {

    val penyakit=repository.penyakit

    val message=repository.message

    fun favoritePenyakit(penyakit:PenyakitTumbuhan)=repository.favoritePenyakit(penyakit)

    var penyakitId=""
        set(value) {
            field=value
            getPenyakit(value)
        }

    private fun getPenyakit(penyakitId: String)=repository.getPenyakit(penyakitId)
}