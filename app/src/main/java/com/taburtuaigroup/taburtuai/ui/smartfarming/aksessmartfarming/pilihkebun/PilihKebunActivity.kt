package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.pilihkebun

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Kebun
import com.taburtuaigroup.taburtuai.core.domain.model.Petani
import com.taburtuaigroup.taburtuai.databinding.ActivityPilihKebunBinding
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun.KebunActivity
import com.taburtuaigroup.taburtuai.core.util.EXTRA_PETANI
import com.taburtuaigroup.taburtuai.core.util.KEBUN_ID
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.penjadwalan.SchedulerActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.fav.FavActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PilihKebunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPilihKebunBinding
    private val viewModel: PilihKebunViewModel by viewModels()
    private var idPetani: String? = null
    private lateinit var kebunAdapter: KebunAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPilihKebunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val layoutManager = GridLayoutManager(this, 2)
        binding.rvKebun.layoutManager = layoutManager

        kebunAdapter = KebunAdapter { kebun ->
            val intent = Intent(this, KebunActivity::class.java)
            intent.putExtra(KEBUN_ID, kebun.id_kebun)
            startActivity(intent)
        }
        binding.rvKebun.adapter = kebunAdapter

        viewModel.petani = intent.getParcelableExtra<Petani>(EXTRA_PETANI)
        val x = viewModel.petani
        if (x != null) {
            binding.toolbarTitle.text = getString(R.string.kebun_petani, x.nama_petani)
            idPetani = x.id_petani
            viewModel.getAllKebun(idPetani = x.id_petani).observe(this) {
                handleDataKebun(it)
            }

        } else {
            finish()
        }
    }

    private fun handleDataKebun(data: Resource<List<Kebun>>) {
        when (data) {
            is Resource.Loading -> showLoading(true)
            is Resource.Success -> {
                showLoading(false)
                val list = data.data
                list?.let { showRecyclerView(it) }
            }
            is Resource.Error -> {
                showLoading(false)
                data.message?.let { ToastUtil.makeToast(baseContext, it) }
            }
        }
    }

    private fun showLoading(it: Boolean) {
        binding.pbLoading.visibility = if (it) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.item_serach)

        val unwrappedDrawable: Drawable? = searchItem?.icon
        val wrappedDrawable = unwrappedDrawable?.let { DrawableCompat.wrap(it) }
        if (wrappedDrawable != null) {
            DrawableCompat.setTint(
                wrappedDrawable,
                getColor(R.color.green)
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
                        .observe(this@PilihKebunActivity) {
                            handleDataKebun(it)
                        }
                }
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_scheduler -> {
                val intent = Intent(this, SchedulerActivity::class.java)
                intent.putExtra(EXTRA_PETANI,viewModel.petani)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showRecyclerView(kebun: List<Kebun>) {
        kebunAdapter.submitList(kebun)
        kebunAdapter.notifyDataSetChanged()
        binding.rvKebun.visibility = if (kebun.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }
}