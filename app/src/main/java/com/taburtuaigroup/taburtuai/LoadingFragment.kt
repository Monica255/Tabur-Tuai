package com.taburtuaigroup.taburtuai

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout

class LoadingFragment(context: Context): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_loading)
        window?.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(R.color.white_transparent)
    }
}