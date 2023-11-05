package com.taburtuaigroup.taburtuai.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import com.taburtuaigroup.taburtuai.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*


class DataConverter {
    companion object {

        @SuppressLint("UseCompatLoadingForDrawables")
        fun getWeatherDrawable(code:Int, context:Context):Drawable{
            return when (code) {
                0-> context.resources.getDrawable(R.drawable.icon_cerah,context.resources.newTheme())
                1-> context.resources.getDrawable(R.drawable.icon_cerah_berawan,context.resources.newTheme())
                2 -> context.resources.getDrawable(R.drawable.icon_cerah_berawan,context.resources.newTheme())
                3 -> context.resources.getDrawable(R.drawable.icon_berawan,context.resources.newTheme())
                4 -> context.resources.getDrawable(R.drawable.icon_berawan_tebal,context.resources.newTheme())
                5-> context.resources.getDrawable(R.drawable.icon_udara_kabur,context.resources.newTheme())
                10 -> context.resources.getDrawable(R.drawable.icon_berasap,context.resources.newTheme())
                45 -> context.resources.getDrawable(R.drawable.icon_kabut,context.resources.newTheme())
                60 -> context.resources.getDrawable(R.drawable.icon_hujan_ringan,context.resources.newTheme())
                61 -> context.resources.getDrawable(R.drawable.icon_hujan_sedang,context.resources.newTheme())
                63 -> context.resources.getDrawable(R.drawable.icon_hujan_lebat,context.resources.newTheme())
                80 -> context.resources.getDrawable(R.drawable.icon_hujan_lokal,context.resources.newTheme())
                95 -> context.resources.getDrawable(R.drawable.icon_hujan_petir,context.resources.newTheme())
                97 -> context.resources.getDrawable(R.drawable.icon_hujan_petir,context.resources.newTheme())
                else -> context.resources.getDrawable(R.drawable.icon_placeholder_2,context.resources.newTheme())
            }
        }
        fun getWindDirection(data:String,ctx:Context):String{
            val x=data.split(" ")
            return when(x[0]){
                "N"->ctx.getString(R.string.utara)
                "NNE"->ctx.getString(R.string.utara_timur_laut)
                "NE"->ctx.getString(R.string.timur_laut)
                "ENE"->ctx.getString(R.string.timur_timur_laut)
                "E"->ctx.getString(R.string.timur)
                "ESE"->ctx.getString(R.string.timur_tenggara)
                "SE"->ctx.getString(R.string.tenggara)
                "SSE"->ctx.getString(R.string.selatan_tenggara)
                "S"->ctx.getString(R.string.selatan)
                "SSW"->ctx.getString(R.string.selatan_barat_daya)
                "SW"->ctx.getString(R.string.barat_daya)
                "WSW"->ctx.getString(R.string.barat_barat_daya)
                "W"->ctx.getString(R.string.barat)
                "NW"->ctx.getString(R.string.barat_laut)
                "WNW"->ctx.getString(R.string.barat_barat_laut)
                "NNW"->ctx.getString(R.string.utara_barat_laut)
                else->ctx.getString(R.string.berubah_ubah)
            }
        }


    }
}