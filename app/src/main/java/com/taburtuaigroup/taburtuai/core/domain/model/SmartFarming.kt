package com.taburtuaigroup.taburtuai.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Device(
    var id_device: String = "",
    var name: String = "",
    var state: Int = 2,
    var type: String = ""
)

data class UserData(
    var email: String = "",
    var name: String = "",
    var phone_number: String = "",
    var uid: String = "",
    var img_profile: String = ""

)

data class MonitoringKebun(
    var temperatur: Int? = null,
    var kelembaban_tanah: Int? = null,
    var ph_tanah: Int? = null,
    var humidity: Int? = null,
    var arah_angin: String = "",
    var kecepatan_angin: Int? = null
)

data class Kebun(
    var id_kebun: String = "",
    var id_petani: String = "",
    var img_kebun: String = "",
    var kota: String = "",
    var provinsi: String = "",
    var luas_kebun: Int = 0,
    var satuan_luas: String = "",
    var nama_kebun: String = "",
)

@Parcelize
data class Petani(
    var nama_petani: String = "",
    var id_petani: String = "",
    var password_petani: String = "",
):Parcelable

data class Masukan(
    var jenis_masukan: String = "",
    var masukan: String = "",
    var email: String = "anonim",
    var time: String = ""
)

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


data class WeatherTime(
    var humidity:String="",
    var temperature:String="",
    var windSpeed:String="",
    var windDirection:String="",
    var weatherCode:Int=-1,
    var weatherName:String=""
)

data class DailyWeather(
    var date: Mdate,
    var diniHari: WeatherTime,
    var pagiHari: WeatherTime,
    var siangHari: WeatherTime,
    var malamHari: WeatherTime
)

data class WeatherForcast(
    val city:String="",
    val province:String="",
    val dailyWeather:List<DailyWeather>?=null
)

data class Mdate(
    var year:Int,
    var month:Int,
    var date:Int,
    var hour:Int?=null
)