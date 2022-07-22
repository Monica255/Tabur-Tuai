package com.example.taburtuai.ui.smartfarming.kebun

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.taburtuai.R
import com.example.taburtuai.data.Device
import com.example.taburtuai.util.TextFormater
import java.math.RoundingMode.valueOf

class ControlDeviceView : RelativeLayout {

    private lateinit var tvItemName: TextView
    private lateinit var tvState: TextView
    private lateinit var drawable: ImageView
    private lateinit var bg: ConstraintLayout
    private lateinit var cv: CardView

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
        //setBackgroundResource(R.drawable.bg_card_home)
        gravity = Gravity.CENTER
    }

    private fun init(context: Context) {
        val rootView = inflate(context, R.layout.view_realtime, this)


        tvItemName = rootView.findViewById(R.id.tv_controlling)
        tvState = rootView.findViewById(R.id.tv_controlling_state)
        drawable = rootView.findViewById(R.id.icon_controlling)
        bg = rootView.findViewById(R.id.item_controlliing)
        cv = rootView.findViewById(R.id.cv_controlling_state)

    }

    fun setData(data: Device) {

        tvItemName.text = if(data.name!="")TextFormater.toTitleCase(data.name) else data.id_device
        when (data.state) {
            0 -> {
                turnOffView()
                tvState.text = context.getString(R.string.mati)
            }
            1 -> {
                turnOnView()
                tvState.text = context.getString(R.string.nyala)
            }
            else -> {
                disableView()
                tvState.text = context.getString(R.string.unknown)
            }
        }


    }

    fun turnOnView() {
        alpha=1F
        tvItemName.setTextColor(context.getColor(R.color.white))
        tvState.setTextColor(context.getColor(R.color.green))
        bg.setBackgroundColor(context.getColor(R.color.green))
        cv.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.white))
        DrawableCompat.setTint(
            DrawableCompat.wrap(drawable.drawable),
            ContextCompat.getColor(context, R.color.white)
        )
    }

    fun turnOffView() {
        alpha=1F
        isClickable=false
        tvItemName.setTextColor(context.getColor(R.color.green))
        tvState.setTextColor(context.getColor(R.color.white))
        bg.setBackgroundColor(context.getColor(R.color.white))
        cv.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.green))
        DrawableCompat.setTint(
            DrawableCompat.wrap(drawable.drawable),
            ContextCompat.getColor(context, R.color.green)
        )
    }


    fun disableView(){
        turnOffView()
        alpha=0.5F
    }
}