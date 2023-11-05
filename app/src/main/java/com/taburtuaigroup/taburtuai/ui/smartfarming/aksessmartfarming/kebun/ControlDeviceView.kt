package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.Device
import com.taburtuaigroup.taburtuai.core.util.TextFormater

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

        tvItemName.text =
            if (data.name != "" && !data.name.contains("cctv", true)) {
                TextFormater.toTitleCase(data.name)
            } else if (data.name.contains("cctv", true)) {
                TextFormater.toTitleCaseCCTV(data.name)
            } else data.id_device


        drawable.setImageDrawable(
            when (data.type.lowercase()) {
                "pompa" -> AppCompatResources.getDrawable(context, R.drawable.icon_pompa)
                "lampu" -> AppCompatResources.getDrawable(context, R.drawable.icon_lightbulb)
                "sprinkler" -> AppCompatResources.getDrawable(context, R.drawable.icon_sprinkle)
                "drip" -> AppCompatResources.getDrawable(context, R.drawable.icon_drip)
                "fogger" -> AppCompatResources.getDrawable(context, R.drawable.icon_fogger)
                "sirine" -> AppCompatResources.getDrawable(context, R.drawable.icon_siren)
                "cctv" -> AppCompatResources.getDrawable(context, R.drawable.icon_cctv_1)
                else -> AppCompatResources.getDrawable(context, R.drawable.icon_placeholder_2)
            }
        )

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

    private fun turnOnView() {
        alpha = 1F
        tvItemName.setTextColor(context.getColor(R.color.white))
        tvState.setTextColor(context.getColor(R.color.green))
        bg.setBackgroundColor(context.getColor(R.color.green))
        cv.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.white))
        DrawableCompat.setTint(
            DrawableCompat.wrap(drawable.drawable).mutate(),
            ContextCompat.getColor(context, R.color.white)
        )
    }

    private fun turnOffView() {
        alpha = 1F
        isClickable = false
        tvItemName.setTextColor(context.getColor(R.color.green))
        tvState.setTextColor(context.getColor(R.color.white))
        bg.setBackgroundColor(context.getColor(R.color.white))
        cv.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.green))
        DrawableCompat.setTint(
            DrawableCompat.wrap(drawable.drawable).mutate(),
            ContextCompat.getColor(context, R.color.green)
        )
    }


    private fun disableView() {
        turnOffView()
        alpha = 0.5F
    }
}