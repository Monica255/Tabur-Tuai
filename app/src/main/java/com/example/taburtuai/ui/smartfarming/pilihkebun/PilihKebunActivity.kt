package com.example.taburtuai.ui.smartfarming.pilihkebun

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.taburtuai.R
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.data.Kebun
import com.example.taburtuai.databinding.ActivityPilihKebunBinding
import com.example.taburtuai.ui.smartfarming.kebun.KebunActivity
import com.example.taburtuai.util.IS_ALWAYS_LOGIN_PETANI
import com.example.taburtuai.util.KEBUN_ID
import com.example.taburtuai.util.ToastUtil

class PilihKebunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPilihKebunBinding
    private lateinit var viewModel: PilihKebunViewModel
    private var idPetani: String? = null
    private lateinit var kebunAdapter:KebunAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPilihKebunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val layoutManager = GridLayoutManager(this, 2)
        binding.rvKebun.layoutManager = layoutManager

        viewModel = ViewModelProvider(
            this, ViewModelFactory.getInstance(application)
        )[PilihKebunViewModel::class.java]

        kebunAdapter = KebunAdapter { kebun ->
            val intent = Intent(this, KebunActivity::class.java)
            intent.putExtra(KEBUN_ID, kebun.id_kebun)
            startActivity(intent)
        }
        binding.rvKebun.adapter = kebunAdapter


        /*viewModel.idPetani.observe(this) {
            if(it!=null){
                binding.toolbarLayout.title = getString(R.string.kebun_petani, it)
                idPetani = it
                viewModel.getAllKebun(idPetani = it)
            }else{
                finish()
            }
        }*/

        viewModel.petani.observe(this) {
            if(it!=null){
                binding.toolbarLayout.title = getString(R.string.kebun_petani, it.nama_petani)
                idPetani = it.id_petani
                viewModel.getAllKebun(idPetani = it.id_petani)
            }else{
                finish()
            }
        }

        viewModel.kebunPetani.observe(this) {
            showRecyclerView(it)
        }

        viewModel.isConnected.observe(this) {
            ToastUtil.showInternetSnackbar(this,binding.root,it)

        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pilih_kebun, menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.item_serach)

        val unwrappedDrawable: Drawable? = searchItem?.icon
        val wrappedDrawable = unwrappedDrawable?.let { DrawableCompat.wrap(it) }
        if (wrappedDrawable != null) {
            DrawableCompat.setTint(
                wrappedDrawable,
                getColor(R.color.white)
            )
        }

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
        kebunAdapter.submitList(kebun)
        kebunAdapter.notifyDataSetChanged()
        binding.rvKebun.visibility=if(kebun.isNotEmpty())View.VISIBLE else View.GONE
    }



    override fun onStart() {
        super.onStart()
        viewModel.isConnected.value?.let {
            ToastUtil.showInternetSnackbar(
                this,
                binding.root,
                it
            )
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