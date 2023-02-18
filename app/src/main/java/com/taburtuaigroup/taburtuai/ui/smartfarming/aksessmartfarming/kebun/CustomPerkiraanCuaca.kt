package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.DailyWeather
import com.taburtuaigroup.taburtuai.core.domain.model.Mdate
import com.taburtuaigroup.taburtuai.core.util.DataConverter
import com.taburtuaigroup.taburtuai.core.util.TextFormater
import java.util.*

class CustomPerkiraanCuaca: RelativeLayout {
    private lateinit var tvPerkiraanCuacaDini: TextView
    private lateinit var drawableDini: ImageView
    private lateinit var tvPerkiraanCuacaPagi: TextView
    private lateinit var drawablePagi: ImageView
    private lateinit var tvPerkiraanCuacaSiang: TextView
    private lateinit var drawableSiang: ImageView
    private lateinit var tvPerkiraanCuacaMalam: TextView
    private lateinit var drawableMalam: ImageView

    private lateinit var llDiniHari: LinearLayout
    private lateinit var llPagi: LinearLayout
    private lateinit var llSiang: LinearLayout
    private lateinit var llMalam: LinearLayout

    private lateinit var bg: LinearLayout

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        gravity = Gravity.CENTER
    }

    private fun init(context: Context) {
        val rootView = inflate(context, R.layout.view_perkiraan_cuaca, this)

        tvPerkiraanCuacaDini = rootView.findViewById(R.id.tv_perkiraan_cuaca_dini_hari)
        drawableDini = rootView.findViewById(R.id.icon_perkiraan_cuaca_dini_hari)
        tvPerkiraanCuacaPagi = rootView.findViewById(R.id.tv_perkiraan_cuaca_pagi)
        drawablePagi = rootView.findViewById(R.id.icon_perkiraan_cuaca_pagi)
        tvPerkiraanCuacaSiang = rootView.findViewById(R.id.tv_perkiraan_cuaca_siang)
        drawableSiang = rootView.findViewById(R.id.icon_perkiraan_cuaca_siang)
        tvPerkiraanCuacaMalam = rootView.findViewById(R.id.tv_perkiraan_cuaca_malam)
        drawableMalam= rootView.findViewById(R.id.icon_perkiraan_cuaca_malam)

        llDiniHari=rootView.findViewById(R.id.ll_perkiraan_cuaca_dini_hari)
        llPagi=rootView.findViewById(R.id.ll_perkiraan_cuaca_pagi)
        llSiang=rootView.findViewById(R.id.ll_perkiraan_cuaca_siang)
        llMalam=rootView.findViewById(R.id.ll_perkiraan_cuaca_malam)
        bg = rootView.findViewById(R.id.ll_perkiraan_cuaca_pagi)

    }


    fun setData(data: DailyWeather) {
        tvPerkiraanCuacaDini.text= TextFormater.getWeatherName(data.diniHari.weatherCode,context)
        tvPerkiraanCuacaPagi.text= TextFormater.getWeatherName(data.pagiHari.weatherCode,context)
        tvPerkiraanCuacaSiang.text= TextFormater.getWeatherName(data.siangHari.weatherCode,context)
        tvPerkiraanCuacaMalam.text= TextFormater.getWeatherName(data.malamHari.weatherCode,context)

        drawableDini.setImageDrawable(
            DataConverter.getWeatherDrawable(data.diniHari.weatherCode,context)
        )
        drawablePagi.setImageDrawable(
            DataConverter.getWeatherDrawable(data.pagiHari.weatherCode,context)
        )
        drawableSiang.setImageDrawable(
            DataConverter.getWeatherDrawable(data.siangHari.weatherCode,context)
        )
        drawableMalam.setImageDrawable(
            DataConverter.getWeatherDrawable(data.malamHari.weatherCode,context)
        )

        markCurrentTime(data.date)
    }

    private fun markCurrentTime(data: Mdate){
        val rightNow = Calendar.getInstance()
        val year=rightNow.get(Calendar.YEAR)
        val month=rightNow.get(Calendar.MONTH)+1
        val day=rightNow.get(Calendar.DAY_OF_MONTH)
        val hour =rightNow.get(Calendar.HOUR_OF_DAY)
        val currentTime= Mdate(year,month,day,null)
        if(data==currentTime){
            if(hour<6){
                llDiniHari.background=AppCompatResources.getDrawable(context, R.drawable.bg_custom_perkiraan_cuaca)
                llPagi.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
                llSiang.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
                llMalam.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
            }else if(hour<12){
                llPagi.background=AppCompatResources.getDrawable(context, R.drawable.bg_custom_perkiraan_cuaca)
                llDiniHari.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
                llSiang.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
                llMalam.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
            }else if(hour<18){
                llSiang.background=AppCompatResources.getDrawable(context, R.drawable.bg_custom_perkiraan_cuaca)
                llDiniHari.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
                llPagi.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
                llMalam.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
            }else if(hour<24){
                llMalam.background=AppCompatResources.getDrawable(context, R.drawable.bg_custom_perkiraan_cuaca)
                llDiniHari.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
                llPagi.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
                llSiang.setBackgroundColor(rootView.context.getColor(R.color.super_light_blue))
            }
        }
    }

}