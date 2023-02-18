package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Kebun
import com.taburtuaigroup.taburtuai.databinding.ActivityKebunBinding
import com.taburtuaigroup.taburtuai.core.util.Event
import com.taburtuaigroup.taburtuai.core.util.KEBUN_ID
import com.taburtuaigroup.taburtuai.core.util.TextFormater
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KebunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKebunBinding
    private lateinit var kebunId: String
    private val viewModel: KebunViewModel by viewModels()
    private var province=""
    private var city=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKebunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter


        TabLayoutMediator(binding.tab, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()


        kebunId = intent.getStringExtra(KEBUN_ID) ?: ""
        viewModel.kebunId = kebunId

        viewModel.getMonitoringKebun(kebunId).observe(this){
            when(it){
                is Resource.Loading->{}
                is Resource.Success->{
                    viewModel.monitoring.value=it.data
                }
                is Resource.Error->{
                    ToastUtil.makeToast(baseContext,it.message.toString())
                }
            }
        }

        viewModel.getControllingKebun(kebunId).observe(this){
            when(it){
                is Resource.Loading->{}
                is Resource.Success->{
                    viewModel.controlling.value=it.data
                }
                is Resource.Error->{
                    ToastUtil.makeToast(baseContext,it.message.toString())
                }
            }
        }

        viewModel.getKebun(kebunId).observe(this){
            when(it){
                is Resource.Loading->{}
                is Resource.Success->{
                    if (it.data != null) {
                        setData(it.data)
                        province=it.data.provinsi.trim().replace(" ","-")
                        city=it.data.kota.trim().replace(" ","-")
                        lifecycleScope.launch {
                            viewModel.getWeatherForecast(province!!, city!!).observe(this@KebunActivity) { it->
                                if(it!=null){
                                    when(it){
                                        is Resource.Success->{
                                            it?.data?.dailyWeather?.let { it1 ->
                                                viewModel.weatherForcastData.value=it1
                                            }
                                        }
                                        is Resource.Loading->{
                                            Log.d("TAG","Loading")
                                        }
                                        is Resource.Error->{
                                            ToastUtil.makeEventToast(this@KebunActivity, Pair(true, Event("Gagal mendapatkan data perkiraan cuaca")))
                                        }
                                    }
                                }
                            }
                        }
                    }else {
                        //TODO show user there is no data kebun
                    }
                }
                is Resource.Error->{
                    ToastUtil.makeToast(baseContext,it.message.toString())
                }
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

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
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