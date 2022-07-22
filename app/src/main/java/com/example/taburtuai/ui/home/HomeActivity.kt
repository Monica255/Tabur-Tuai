package com.example.taburtuai.ui.home


import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.taburtuai.R
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.databinding.ActivityHomeBinding
import com.example.taburtuai.ui.smartfarming.loginsmartfarming.LoginSmartFarmingActivity
import com.example.taburtuai.ui.smartfarming.pilihkebun.PilihKebunActivity
import com.example.taburtuai.util.BitmapCrop
import com.example.taburtuai.util.SESI_PETANI_ID
import com.example.taburtuai.util.ToastUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.squareup.picasso.Picasso


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBar()

        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(this))[HomeViewModel::class.java]


        binding.btSmartFarming.setOnClickListener {
            val prefManager =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
            val petaniId = prefManager.getString(SESI_PETANI_ID, "")
            val alwaysLogin = prefManager.getBoolean("always_login_petani", false)

            if (alwaysLogin) {
                if (petaniId != "" && petaniId != null) {
                    viewModel.autoLoginPetani(petaniId)
                    startActivity(Intent(this, PilihKebunActivity::class.java))
                } else {
                    Toast.makeText(this, "Belum ada sesi petani", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginSmartFarmingActivity::class.java))
                }
            } else {
                startActivity(Intent(this, LoginSmartFarmingActivity::class.java))
            }

        }

        viewModel.isConnected.observe(this) {
            ToastUtil.showInternetSnackbar(
                this,
                binding.root,
                it
            )
        }

    }

    override fun onStart() {
        super.onStart()
        ToastUtil.sb = null
        viewModel.isConnected.value?.let {
            ToastUtil.showInternetSnackbar(
                this,
                binding.root,
                it
            )
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        var drawable: Drawable?
        val menuItem = menu?.findItem(R.id.action_profile)

        viewModel.userData.observe(this) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(it?.name)
                .setPhotoUri(Uri.parse(it?.img_profile))
                .build()
            FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
            binding.tvHalo.text =
                getString(R.string.halo, it?.name ?: "")
            val uri: Uri? =
                if (it?.img_profile?.isNotEmpty() == true) Uri.parse(it.img_profile) else null

            if (uri != null) {
                Picasso.get().load(uri).into(object : com.squareup.picasso.Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        drawable = BitmapDrawable(
                            resources,
                            bitmap?.let { BitmapCrop.getCroppedBitmap(it) })
                        menuItem?.icon = drawable
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        Log.d("TAG", e.toString())
                    }
                })
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                /*val settingIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingIntent)*/
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
    }
}