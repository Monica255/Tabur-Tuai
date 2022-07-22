package com.example.taburtuai.util

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.taburtuai.LoadingFragment

open class LoadingUtils {
    companion object {
        private var jarvisLoading: LoadingFragment? = null

        fun showLoading(
            context: Context?,
            isCancelable: Boolean
        ) {
            hideLoading()
            if (context != null) {
                try {
                    jarvisLoading = LoadingFragment(context)
                    jarvisLoading?.let { jarvisLoader->
                        jarvisLoader.setCanceledOnTouchOutside(true)
                        jarvisLoader.setCancelable(isCancelable)
                        jarvisLoader.show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun hideLoading() {
            if (jarvisLoading!=null && jarvisLoading?.isShowing!!) {
                jarvisLoading = try {
                    jarvisLoading?.dismiss()
                    null
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}