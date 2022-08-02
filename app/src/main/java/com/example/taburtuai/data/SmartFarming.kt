package com.example.taburtuai.data

data class Device(
    var id_device:String="",
    var name:String="",
    var state:Int=2,
    var type:String=""
)

data class UserData(
    var email:String="",
    var name:String="",
    var phone_number:String="",
    var uid:String="",
    var img_profile:String=""

)

data class MonitoringKebun(
    var temperatur:Int?=null,
    var kelembaban_tanah:Int?=null,
    var ph_tanah:Int?=null,
    var humidity:Int?=null,
    var arah_angin:String="",
    var kecepatan_angin:Int?=null
)

data class Kebun(
    var id_kebun:String="",
    var id_petani:String="",
    var img_kebun:String="",
    var kota:String="",
    var provinsi:String="",
    var luas_kebun:Int=0,
    var satuan_luas:String="",
    var nama_kebun:String="",
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
    var nama_petani:String="",
    var id_petani:String="",
    var password_petani:String="",
    )

data class Masukan(
    var jenis_masukan:String="",
    var masukan:String="",
    var email:String="anonim",
    var time:String=""
)

