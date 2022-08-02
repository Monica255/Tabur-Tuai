package com.example.taburtuai.util

import android.content.Context
import android.util.Log
import com.example.taburtuai.R

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
                var list = data.lowercase().trim().split(" ")
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
                toTitleCase("$kota, $provinsi")
            }else if(kota!=""){
                toTitleCase(kota)
            }else if(provinsi!=""){
                return provinsi
            }else ctx.getString(R.string.tidak_ada_data_lokasi)
        }
    }


}