package com.taburtuaigroup.taburtuai.core.util

import android.content.Context
import com.taburtuaigroup.taburtuai.R

class TextFormater {
    companion object{
        fun toTitleCase(data:String?):String{
            var newString=""
            if(data!=null){
                val x=data.lowercase().trim().split(" ")
                for (i in x){
                    newString= "$newString ${
                        i.lowercase()
                            .replaceFirstChar(Char::titlecase)
                    }"
                }
            }
            return newString.trim()
        }

        fun toTitleCaseCCTV(data:String?):String{
            var newString = ""
            if (data!=null){
                val list = data.lowercase().trim().split(" ")
                for (i in list) {

                    newString = "$newString ${
                        if (i == "cctv")
                            i.uppercase()
                        else
                            i.lowercase().replaceFirstChar(Char::titlecase)
                    }"
                }
            }
            return newString
        }

        fun getLuasKebun(luas:Int,satuan:String,ctx:Context):String{
            if(luas==0){
                return ctx.getString(R.string.tidak_ada_data_luas_kebun)
            }else{
                return when (satuan) {
                    "meter persegi" -> {
                        "$luas \u33A1"
                    }
                    "hektar" -> {
                        "$luas Ha"
                    }
                    "are" ->{
                        "$luas Are"
                    }
                    else -> {
                        "$luas $satuan"
                    }
                }

            }
        }

        fun getLokasiKebun(kota:String,provinsi:String,ctx:Context):String{
            return if (kota!="" && provinsi!=""){
                "${toTitleCase(kota)},\n${toTitleCase(provinsi)}"
            }else if(kota!=""){
                toTitleCase(kota)
            }else if(provinsi!=""){
                return toTitleCase(provinsi)
            }else ctx.getString(R.string.tidak_ada_data_lokasi)
        }

        fun getWeatherName(code:Int,context:Context):String{
            return when(code){
                0 ->context.getString(R.string.cerah)
                1 -> context.getString(R.string.cerah_berawan)
                2 -> context.getString(R.string.cerah_berawan)
                3 -> context.getString(R.string.berawan)
                4 -> context.getString(R.string.berawan)
                5 -> context.getString(R.string.udara_kabur)
                10 -> context.getString(R.string.asap)
                45 -> context.getString(R.string.kabut)
                60 -> context.getString(R.string.hujan_ringan)
                61 -> context.getString(R.string.hujan_sedang)
                63 -> context.getString(R.string.hujan_lebat)
                80 -> context.getString(R.string.hujan_lokal)
                95 -> context.getString(R.string.hujan_petir)
                97 -> context.getString(R.string.hujan_petir)
                else->context.getString(R.string.hyphen)
            }
        }
    }



}