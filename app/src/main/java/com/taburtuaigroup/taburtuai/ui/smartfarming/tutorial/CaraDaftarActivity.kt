package com.taburtuaigroup.taburtuai.ui.smartfarming.tutorial

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.databinding.ActivityCaraDaftarBinding
import com.taburtuaigroup.taburtuai.core.util.EMAIL


class CaraDaftarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaraDaftarBinding
    private val email = "taburtuaigroup@gmail.com"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaraDaftarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        binding.tvEmail.text = email
        binding.btCopy.setOnClickListener {
            copyToClipBoard()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    private fun copyToClipBoard() {
        val clipboard: ClipboardManager? =
            ContextCompat.getSystemService(this, ClipboardManager::class.java)
        val clip: ClipData? =
            ClipData.newPlainText(EMAIL, email)

        if (clip != null) {
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()

        }
    }
}