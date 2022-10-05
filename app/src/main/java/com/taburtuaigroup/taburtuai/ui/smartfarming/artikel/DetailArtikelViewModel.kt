package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.data.Repository

class DetailArtikelViewModel(private val repository: Repository) :ViewModel() {

    val artikel=repository.artikel

    fun favoriteArtikel(artikel: Artikel,favorite:Boolean)=repository.favoriteArtikel(artikel)

    val mesage=repository.message

    var artikelId=""
        set(value) {
            field=value
            getArtikel(value)
        }

    private fun getArtikel(artikelId: String)=repository.getArtikel(artikelId)
}