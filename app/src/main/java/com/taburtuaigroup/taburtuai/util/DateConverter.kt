package com.taburtuaigroup.taburtuai.util

import android.content.Context
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.data.Mdate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*

object DateConverter {
    fun convertStringToData(data:String,withHour:Boolean=true): Mdate {
        val year=data.substring(0,4).toInt()
        val month=data.substring(4,6).replaceFirst("^0+(?!$)", "").toInt()
        val day=data.substring(6,8).replaceFirst("^0+(?!$)", "").toInt()
        val hour=data.substring(8,10).replaceFirst("^0+(?!$)", "").toInt()


        return Mdate(year,month,day,if (withHour)hour else null)
    }
    fun convertMillisToString(timeMillis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    fun convertMillisToDate(timeMillis: Long,ctx:Context):String{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeMillis
        val sdf = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault())

        var list=sdf.format(calendar.time).split(",")

        var output=StringBuilder().append(when(list[0]){
            "Monday"->ctx.getString(R.string.monday)
            "Tuesday"->ctx.getString(R.string.tuesday)
            "Wednesday"->ctx.getString(R.string.wednesday)
            "Thursday"->ctx.getString(R.string.thursday)
            "Friday"->ctx.getString(R.string.friday)
            "Saturday"->ctx.getString(R.string.saturday)
            "Sunday"->ctx.getString(R.string.Sunday)
            else->""
        }).append(", ").append(list[1].trim())


        return output.toString()
    }

}