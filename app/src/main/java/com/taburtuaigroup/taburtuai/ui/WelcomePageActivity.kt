package com.taburtuaigroup.taburtuai.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.taburtuaigroup.taburtuai.databinding.ActivityWelcomePageBinding
import com.taburtuaigroup.taburtuai.ui.loginsignup.LoginSignupActivity

class WelcomePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWelcomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btMulai.setOnClickListener {
            startActivity(Intent(this, LoginSignupActivity::class.java))
        }
    }
}