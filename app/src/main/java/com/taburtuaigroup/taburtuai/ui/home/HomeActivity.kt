package com.taburtuaigroup.taburtuai.ui.home


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.UserData
import com.taburtuaigroup.taburtuai.databinding.ActivityHomeBinding
import com.taburtuaigroup.taburtuai.ui.profile.ProfileActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.SmartFarmingActivity
import com.taburtuaigroup.taburtuai.util.ToastUtil


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private var data: UserData = UserData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBar()

        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory.getInstance(application)
            )[HomeViewModel::class.java]


        binding.btSmartFarming.setOnClickListener {
            startActivity(Intent(this, SmartFarmingActivity::class.java))
        }

        viewModel.userData.observe(this) {
            if (it != null) setData(it)
        }

        binding.imgProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setData(data: UserData) {
        this.data=data
        /*val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(data?.name)
            .setPhotoUri(Uri.parse(data?.img_profile))
            .build()
        FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)*/
        binding.tvHalo.text =
            getString(R.string.halo, data.name ?: "")
        val uri: Uri? =
            if (data.img_profile.isNotEmpty()) Uri.parse(data.img_profile) else null
        setDrawable(uri)

    }

    private fun setDrawable(uri:Uri?){
        if (uri != null) {
            Glide.with(binding.root)
                .load(uri)
                .placeholder(R.drawable.placeholder)
                .into(binding.imgProfile)
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
    }
}