package com.taburtuaigroup.taburtuai.util

import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan

const val KEBUN_ID="kebunId"
const val LAST_UPDATE="last_update"
const val SESI_PETANI_ID="sesi_petani_id"
const val IS_ALWAYS_LOGIN_PETANI="always_login_petani"
const val LAST_DATE_SEND_FEEDBACK="last_date_send_feedback"
const val FEEDBACK_COUNT="feedback_count"
const val ARTIKEL_ID="artikelId"
const val PENYAKIT_ID="penyakit_id"
const val PROFILE_URL="profile_url"
const val UID="uid"
const val ARTIKEL="artikel"
const val PENYAKIT_TUMBUHAN="penyakit_tumbuhan"
const val EMAIL="email"

enum class Kategori {
    ARTIKEL, PENYAKIT
}

enum class KategoriArtikel(val printable:String) {
    SEMUA(""),INFORMASI("informasi"), EDUKASI("edukasi"), LAINNYA("lainnya")
}

sealed class ViewEventsArtikel {
    data class Edit(val entity: Artikel) : ViewEventsArtikel()
    data class Remove(val entity: Artikel) : ViewEventsArtikel()
    data class Rebind(val entity: Artikel) : ViewEventsArtikel()
}

sealed class ViewEventsPenyakit {
    data class Edit(val entity: PenyakitTumbuhan) : ViewEventsPenyakit()
    data class Remove(val entity: PenyakitTumbuhan) : ViewEventsPenyakit()
    data class Rebind(val entity: PenyakitTumbuhan) : ViewEventsPenyakit()
}