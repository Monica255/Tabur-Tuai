package com.example.taburtuai.ui.kebun

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.StringRes
import com.example.taburtuai.R
import com.example.taburtuai.SectionsPagerAdapter
import com.example.taburtuai.databinding.ActivityKebunBinding
import com.google.android.material.tabs.TabLayoutMediator

class KebunActivity : AppCompatActivity() {
    private lateinit var binding:ActivityKebunBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityKebunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBar()
        setData()

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()


    }
    private fun setActionBar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun setData(){
        //TODO fetch data from firebase
        binding.toolbarLayout.title="Kebun 1"
    }

    companion object {

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.monitoring,
            R.string.controlling
        )
    }
}