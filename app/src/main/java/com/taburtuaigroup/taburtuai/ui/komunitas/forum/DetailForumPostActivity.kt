package com.taburtuaigroup.taburtuai.ui.komunitas.forum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.taburtuaigroup.taburtuai.databinding.ActivityDetailForumPostBinding

class DetailForumPostActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailForumPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailForumPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener {
            finish()
        }
    }
}