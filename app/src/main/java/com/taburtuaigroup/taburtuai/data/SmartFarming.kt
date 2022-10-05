package com.taburtuaigroup.taburtuai.data

import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

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

/*data class RealtimeKebun(
    var id_kebun:String="",
    var monitoring:MonitoringKebun?=null,
    var controlling:List<Device>?=null
)*/
/*data class Kebun(
    var nama_kebun: String="",
    var id_kebun:String=""
)*/

data class Petani(
    var nama_petani: String = "",
    var id_petani: String = "",
    var password_petani: String = "",
)

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


data class dummyPenyakit(
    var id_penyakit: String = "",
    var img_header: String = "",
    var title: String = "",
    var deskripsi: String = "",
    var solusi: String = "",
    var author: String = "",
    var timestamp: Long = 0,
    var fav_count: Int = 0
)


data class dummyArtikel(
    var id_artikel: String = "",
    var img_header: String = "",
    var title: String = "",
    var artikel_text: String = "",
    var author: String = "",
    var kategori: String = "",
    var timestamp: Long = 0,
    var fav_count: Int = 0
)

data class HumidityTimeEntity(
   var type:String="",
   var h:String="",
   var datetime:String="",
   var value:String=""
)

data class TemperatureTimeEntity(
    var type:String="",
    var h:String="",
    var datetime:String="",
    var celcius:String="",
    var fahrenheit:String=""
)

data class WeatherTimeEntity(
    var type:String="",
    var h:String="",
    var datetime:String="",
    var code:String="",
    var name:String=""
)
data class WindDirectionTimeEntity(
    var type:String="",
    var h:String="",
    var datetime:String="",
    var code:String="",
    var card:String=""
)

data class WindSpeedTimeEntity(
    var type:String="",
    var h:String="",
    var datetime:String="",
    var kt:String="",
    var mph:String="",
    var kph:String="",
    var ms:String=""
)

data class WeatherEntity(
    var type:String="",
    var h:String="",
    var datetime:String="",
    var value:String="",
    var celcius:String="",
    var fahrenheit:String="",
    var code:String="",
    var name:String="",
    var card:String="",
    var kt:String="",
    var mph:String="",
    var kph:String="",
    var ms:String=""
)

data class Param(
    val id:String="",
    val description:String="",
    val type:String="",
    val times:List<WeatherEntity>
)

data class WeatherData(
    val id:String="",
    val description:String="",
    val domain:String="",
    var params:List<Param>
)

data class ResponseWeather(
    val success:Boolean,
    val message:String?,
    var data:WeatherData
)

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
    var diniHari:WeatherTime,
    var pagiHari:WeatherTime,
    var siangHari:WeatherTime,
    var malamHari:WeatherTime
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