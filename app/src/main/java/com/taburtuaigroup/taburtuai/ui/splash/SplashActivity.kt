package com.taburtuaigroup.taburtuai.ui.splash

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.databinding.ActivitySplashBinding
import com.taburtuaigroup.taburtuai.ui.WelcomePageActivity
import com.taburtuaigroup.taburtuai.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel:SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var versionName: String?
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
            try {
                val intent: Intent = if (viewModel.currentUser != null) {
                    Intent(this, HomeActivity::class.java)
                } else {
                    Intent(this, WelcomePageActivity::class.java)
                }
                startActivity(intent)
                finish()
            }catch (e:Exception){
                Log.d("TAG",e.message.toString())
            }

        }, DELAY_TIME)
    }


    companion object {
        const val DELAY_TIME: Long = 2_000
    }
}