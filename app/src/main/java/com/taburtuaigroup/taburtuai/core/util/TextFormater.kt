package com.taburtuaigroup.taburtuai.core.util

import android.annotation.SuppressLint
import android.content.Context
import com.taburtuaigroup.taburtuai.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.roundToLong

class TextFormater {
    companion object {
        fun toTitleCase(data: String?): String {
            var newString = ""
            if (data != null) {
                val x = data.lowercase().trim().split(" ")
                for (i in x) {
                    newString = "$newString ${
                        i.lowercase()
                            .replaceFirstChar(Char::titlecase)
                    }"
                }
            }
            return newString.trim()
        }

        fun toTitleCaseCCTV(data: String?): String {
            var newString = ""
            if (data != null) {
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

        fun getLuasKebun(luas: Int, satuan: String, ctx: Context): String {
            if (luas == 0) {
                return ctx.getString(R.string.tidak_ada_data_luas_kebun)
            } else {
                return when (satuan) {
                    "meter persegi" -> {
                        "$luas \u33A1"
                    }
                    "hektar" -> {
                        "$luas Ha"
                    }
                    "are" -> {
                        "$luas Are"
                    }
                    else -> {
                        "$luas $satuan"
                    }
                }

            }
        }

        fun getLokasiKebun(kota: String, provinsi: String, ctx: Context): String {
            return if (kota != "" && provinsi != "") {
                "${toTitleCase(kota)},\n${toTitleCase(provinsi)}"
            } else if (kota != "") {
                toTitleCase(kota)
            } else if (provinsi != "") {
                return toTitleCase(provinsi)
            } else ctx.getString(R.string.tidak_ada_data_lokasi)
        }

        fun getWeatherName(code: Int, context: Context): String {
            return when (code) {
                0 -> context.getString(R.string.cerah)
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
                else -> context.getString(R.string.hyphen)
            }
        }

        fun formatLikeCounts(likes: Int): String {
            return if (likes <= 999) {
                likes.toString()
            } else {
                var x = likes
                x /= 100
                var z=x.toDouble()
                z/=10
                z.toString()+" k"
            }
        }

        private fun timestampToLocalTime(timestamp: Long): String {
            return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                TimeZone.getDefault().toZoneId()
            ).toString()

        }

        @SuppressLint("StringFormatMatches")
        fun toPostTime(timestamp: Long, context: Context): String {
            var date = timestampToLocalTime(timestamp)

            val posted: String
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH) + 1
            val day = c.get(Calendar.DAY_OF_MONTH)
            val x = date.split("T")
            val y = stringToDate(x[0])

            val todayDate = LocalDate.of(year, month, day)
            val daysBetween = ChronoUnit.DAYS.between(y, todayDate)

            val time = x[1].substring(0, 5)
            posted = if (x[0] == todayDate.toString()) {
                context.resources.getString(
                    R.string.posted,
                    "${context.resources.getString(R.string.today)}",
                    time
                )
            } else {
                when {
                    daysBetween.toInt() > 7 -> {
                        val r = x[0].split("-")
                        val date =
                            context.resources.getString(R.string.date_format, r[2], r[1], r[0])
                        context.resources.getString(R.string.posted, date, time)
                    }
                    daysBetween.toInt() == 1 -> {
                        context.resources.getString(
                            R.string.posted,
                            "${context.resources.getString(R.string.yesterday)}",
                            time
                        )
                    }
                    else -> {
                        context.resources.getString(
                            R.string.posted_days_ago,
                            "${ChronoUnit.DAYS.between(y, todayDate)}", time
                        )
                    }
                }
            }
            return posted
        }

        fun stringToDate(data: String): LocalDate {
            val x = data.split("-")
            return LocalDate.of(
                x[0].toInt(),
                x[1].toInt(),
                x[2].toInt()
            )
        }

        fun toClockFormat(hour:Int,minute:Int):String{
            val mHour=if(hour<10) "0$hour" else hour.toString()
            val mMinute= if(minute<10) "0$minute" else minute.toString()
            return "$mHour:$mMinute"
        }

    }
}