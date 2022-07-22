package com.example.taburtuai.ui.smartfarming.kebun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.taburtuai.R
import com.example.taburtuai.SectionsPagerAdapter
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.data.Kebun
import com.example.taburtuai.data.RealtimeKebun
import com.example.taburtuai.databinding.ActivityKebunBinding
import com.example.taburtuai.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class KebunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKebunBinding
    private lateinit var kebunId: String
    private lateinit var viewModel: KebunViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKebunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBar()


        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(this))[KebunViewModel::class.java]

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter


        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        kebunId = intent.getStringExtra(KEBUN_ID) ?: ""
        viewModel.kebunId = kebunId



        viewModel.kebun.observe(this) {
            Log.d("TAG", "data " + it.toString())
            if (it != null) setData(it) else {
                //TODO show user there is no data kebun
            }
        }

        viewModel.isConnected.observe(this) {
            ToastUtil.showInternetSnackbar(
                this,
                binding.root,
                it,
                true
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearDataKebun()
    }


    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarLayout.title = getString(R.string.kebun)
    }


    private fun setData(data: Kebun) {
        binding.toolbarLayout.title = data.nama_kebun
        binding.tvKodeKebun.text = data.id_kebun
        binding.tvLokasiKebun.text = TextFormater.getLokasiKebun(data.kota, data.provinsi, this)
        binding.tvLuasKebun.text =
            TextFormater.getLuasKebun(data.luas_kebun, data.satuan_luas, this)
        Glide.with(binding.root)
            .load(data.img_kebun)
            .placeholder(R.drawable.placeholder_kebun)
            .into(binding.imgKebun)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    override fun onStart() {
        super.onStart()
        ToastUtil.sb = null
        viewModel.isConnected.value?.let {
            ToastUtil.showInternetSnackbar(
                this,
                binding.root,
                it,
                true
            )
        }
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.monitoring,
            R.string.controlling
        )
    }
}