package com.taburtuaigroup.taburtuai.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artikel(
    var id_artikel: String = "",
    var img_header: String = "",
    var title: String = "",
    var artikel_text: String = "",
    var author: String = "",
    var kategori: String = "",
    var timestamp: Long = 0,
    var favorites: MutableList<String>?=null,
    var fav_count: Int = 0
) : Parcelable

@Parcelize
data class PenyakitTumbuhan(
    var id_penyakit: String = "",
    var img_header: String = "",
    var title: String = "",
    var deskripsi: String = "",
    var solusi: String = "",
    var author: String = "",
    var timestamp: Long = 0,
    var favorites:  MutableList<String>?=null,
    var fav_count: Int = 0
) : Parcelable

