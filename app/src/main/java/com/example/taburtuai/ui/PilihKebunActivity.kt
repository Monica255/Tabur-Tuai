package com.example.taburtuai.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.example.taburtuai.databinding.ActivityPilihKebunBinding

class PilihKebunActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPilihKebunBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPilihKebunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

    }

    private fun setActionBar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarLayout.title="Pilih Kebun"
    }
}