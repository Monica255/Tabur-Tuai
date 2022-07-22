package com.example.taburtuai.ui.smartfarming.pilihkebun

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.taburtuai.R
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.data.Kebun
import com.example.taburtuai.databinding.ActivityPilihKebunBinding
import com.example.taburtuai.ui.smartfarming.kebun.KebunActivity
import com.example.taburtuai.util.IS_ALWAYS_LOGIN_PETANI
import com.example.taburtuai.util.KEBUN_ID
import com.google.android.material.snackbar.Snackbar

class PilihKebunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPilihKebunBinding
    private lateinit var viewModel: PilihKebunViewModel
    private var sb: Snackbar? = null
    private var showSnackbar = false
    private var idPetani: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPilihKebunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val layoutManager = GridLayoutManager(this, 2)
        binding.rvKebun.layoutManager = layoutManager


        viewModel = ViewModelProvider(
            this, ViewModelFactory.getInstance(this)
        )[PilihKebunViewModel::class.java]

        viewModel.idPetani.observe(this) {
            if(it!=null){
                binding.toolbarLayout.title = getString(R.string.kebun_petani, it)
                idPetani = it
                viewModel.getAllKebun(idPetani = it)
            }else{
                finish()
            }
        }

        viewModel.kebunPetani.observe(this) {
            showRecyclerView(it)
        }

        viewModel.isConnected.observe(this) {
            showInternetSnackbar(it)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pilih_kebun, menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.item_serach)
        val searchView = searchItem.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                idPetani?.let {
                    viewModel.getAllKebun(it, newText?.trim() ?: "")
                }
                return true
            }
        })

        return true
    }

    private fun showRecyclerView(kebun: List<Kebun>) {
        val kebunAdapter = KebunAdapter { kebun ->
            val intent = Intent(this, KebunActivity::class.java)
            intent.putExtra(KEBUN_ID, kebun.id_kebun)
            startActivity(intent)
        }
        kebunAdapter.submitList(kebun)
        binding.rvKebun.adapter = kebunAdapter
        binding.rvKebun.visibility=if(kebun.isNotEmpty())View.VISIBLE else View.GONE
    }

    override fun onStart() {
        super.onStart()
        sb = null
        viewModel.isConnected.value?.let { showInternetSnackbar(it) }
        //if(viewModel.isConnected.value==false){ showInternetSnackbar(false) }
    }

    private fun showInternetSnackbar(isConnected: Boolean) {
        if (!isConnected) {
            showSnackbar = true
            Handler(Looper.getMainLooper()).postDelayed({
                if (showSnackbar) {
                    sb = Snackbar.make(
                        binding.root, "Tidak ada jaringan internet",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("Action", null)
                    sb!!.view.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.snackbar_color_no_inet
                        )
                    )
                    sb!!.setTextColor(resources.getColor(R.color.white, theme))
                    sb!!.show()
                }
            }, 1_400)
        } else {
            showSnackbar = false
            if (sb != null) {
                sb!!.dismiss()
                sb = null
                sb = Snackbar.make(
                    binding.root, "Kembali online",
                    Snackbar.LENGTH_SHORT
                )
                    .setAction("Action", null)
                sb!!.view.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                sb!!.setTextColor(resources.getColor(R.color.green, theme))
                sb!!.show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val isAlwaysLogin=prefManager.getBoolean(IS_ALWAYS_LOGIN_PETANI,false)

        if(!isAlwaysLogin){
            viewModel.logoutPetani()
        }
        super.onBackPressed()
    }



    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarLayout.title = "Pilih Kebun"
    }
}