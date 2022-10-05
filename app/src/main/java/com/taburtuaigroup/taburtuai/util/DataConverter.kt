package com.taburtuaigroup.taburtuai.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import com.taburtuaigroup.taburtuai.R


class DataConverter {
    companion object {
        fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {
            var radius = 0
            var longer = 0
            var resizedBmp: Bitmap
            if (bitmap.height > bitmap.width) {
                radius = bitmap.width
                longer = bitmap.height
                resizedBmp = Bitmap.createBitmap(
                    bitmap,
                    0, (longer - radius) / 2, radius, radius
                )

            } else {
                radius = bitmap.height
                longer = bitmap.width
                resizedBmp = Bitmap.createBitmap(
                    bitmap,
                    (longer - radius) / 2, 0, radius, radius
                )

            }


            val output = Bitmap.createBitmap(
                radius,
                radius, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawCircle(
                (radius / 2).toFloat(), (radius/ 2).toFloat(),
                (radius/ 2).toFloat(), paint
            )
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(resizedBmp, rect, rect, paint)
            return output

        }

        fun getWeatherDrawable(code:Int,context:Context):Drawable{
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