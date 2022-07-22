package com.example.taburtuai.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.taburtuai.R
import com.example.taburtuai.ui.smartfarming.kebun.KebunActivity
import com.google.android.material.snackbar.Snackbar

class ToastUtil {

    companion object {
        var sb: Snackbar? = null
        private var showSnackbar = false
        fun makeToast(ctx: Context, pair: Pair<Boolean, Event<String>>) {
            val msg = pair.second.getContentIfNotHandled()
            if (msg != null) {
                if (!pair.first) {
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                } else {
                    //TODO show error toast
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        fun showInternetSnackbar(
            ctx: Context,
            view: View,
            isConnected: Boolean,
            showTime: Boolean = false
        ) {
            val prefManager =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx)
            val latTime = DateConverter.convertMillisToString(prefManager.getLong(LAST_UPDATE, 0))
            if (!isConnected) {
                showSnackbar = true
                Handler(Looper.getMainLooper()).postDelayed({
                    if (showSnackbar) {
                        sb = Snackbar.make(
                            view,
                            if (showTime) ctx.getString(
                                R.string.tidak_ada_jaringan_update_terakhir,
                                latTime
                            ) else ctx.getString(R.string.tidak_ada_jaringan),
                            Snackbar.LENGTH_INDEFINITE
                        )
                            .setAction("Action", null)
                        sb!!.view.setBackgroundColor(
                            ContextCompat.getColor(
                                ctx,
                                R.color.snackbar_color_no_inet
                            )
                        )
                        sb!!.setTextColor(ctx.resources.getColor(R.color.white, null))
                        sb!!.show()
                    }
                }, 1_400)
            } else {
                showSnackbar = false
                if (sb != null) {
                    sb!!.dismiss()
                    sb = null
                    sb = Snackbar.make(
                        view, ctx.getString(R.string.kembali_online),
                        Snackbar.LENGTH_SHORT
                    )
                        .setAction("Action", null)
                    sb!!.view.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
                    sb!!.setTextColor(ctx.resources.getColor(R.color.green, null))
                    sb!!.show()
                }
            }
        }

    }
}