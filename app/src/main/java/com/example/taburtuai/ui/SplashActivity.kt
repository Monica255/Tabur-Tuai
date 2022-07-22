package com.example.taburtuai.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.taburtuai.databinding.ActivitySplashBinding
import com.example.taburtuai.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //FirebaseAuth.getInstance().signOut()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            var intent=Intent()
            if(FirebaseAuth.getInstance().currentUser!=null){
                intent = Intent(this, HomeActivity::class.java)
            }else{
                intent = Intent(this, WelcomePageActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, DELAY_TIME)
    }



    companion object{
        const val DELAY_TIME:Long=2_000
    }
}