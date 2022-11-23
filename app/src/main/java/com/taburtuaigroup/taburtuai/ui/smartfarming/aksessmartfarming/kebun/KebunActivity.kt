package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.Kebun
import com.taburtuaigroup.taburtuai.databinding.ActivityKebunBinding
import com.taburtuaigroup.taburtuai.util.*


class KebunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKebunBinding
    private lateinit var kebunId: String
    private lateinit var viewModel: KebunViewModel
    private var province=""
    private var city=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKebunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()


        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(application))[KebunViewModel::class.java]

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter


        TabLayoutMediator(binding.tab, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        kebunId = intent.getStringExtra(KEBUN_ID) ?: ""
        viewModel.kebunId = kebunId

        viewModel.kebun.observe(this) {
            if (it != null) {
                setData(it)
                province=it.provinsi.trim().replace(" ","-")
                city=it.kota.trim().replace(" ","-")
                viewModel.getWeatherForcast(province, city)
            }else {
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
        viewModel.kebunId=""
    }


    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        //binding.toolbarLayout.title = getString(R.string.kebun)
    }


    private fun setData(data: Kebun) {
        binding.toolbarTitle.text =if(data.nama_kebun!="")data.nama_kebun else data.id_kebun
        //binding.toolbarLayout.title = if(data.nama_kebun!="")data.nama_kebun else data.id_kebun
        //binding.tvKodeKebun.text = data.id_kebun
        binding.tvLokasiKebun.text = TextFormater.getLokasiKebun(data.kota, data.provinsi, this)
        binding.tvLuasKebun.text =
            TextFormater.getLuasKebun(data.luas_kebun, data.satuan_luas, this)
        Glide.with(binding.root)
            .load(data.img_kebun)
            .placeholder(R.drawable.placeholder_kebun_square)
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