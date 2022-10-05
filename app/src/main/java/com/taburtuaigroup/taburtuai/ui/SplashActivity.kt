package com.taburtuaigroup.taburtuai.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.databinding.ActivitySplashBinding
import com.taburtuaigroup.taburtuai.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var versionName: String?=null
        try {
            versionName = this.packageManager
                .getPackageInfo(this.packageName, 0).versionName
            if (versionName!=null) {
                binding.tvVersion.visibility = View.VISIBLE
                binding.tvVersion.text = getString(R.string.versi, versionName)
            } else binding.tvVersion.visibility = View.GONE
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        /*val content = findViewById<View>(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnDrawListener { false }
        }*/

        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )*/

        Handler(Looper.getMainLooper()).postDelayed({
            val intent: Intent = if (FirebaseAuth.getInstance().currentUser != null) {
                Intent(this, HomeActivity::class.java)
            } else {
                Intent(this, WelcomePageActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, DELAY_TIME)
    }


    companion object {
        const val DELAY_TIME: Long = 2_000
    }
}