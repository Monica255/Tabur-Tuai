package com.taburtuaigroup.taburtuai.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mscheduler(
    val id_scheduler:Int=0,
    val action:Int=0,
    val id_kebun:String="",
    val id_device:String="",
    val id_petani:String="",
    val hour:Int=12,
    val minute:Int=0,
    val active:Boolean=false
):Parcelable