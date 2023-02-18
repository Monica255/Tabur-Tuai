package com.taburtuaigroup.taburtuai.ui.listpetanikebun

import android.app.SearchManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.databinding.ActivityPetaniKebunBinding
import dagger.hilt.android.AndroidEntryPoint


enum class ListType {
    PETANI, KEBUN
}

@AndroidEntryPoint
class PetaniKebunActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPetaniKebunBinding
    private var listType = ListType.PETANI
    private var menuItem: MenuItem? = null
    private lateinit var searchView: SearchView
    private val viewModel: PetaniKebunViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetaniKebunBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        observePetani()
        observeKebun()
    }

    private fun observePetani(filter: String = "") {
        viewModel.getAllPetani(filter).observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                    viewModel.allPetani.value = Resource.Loading()
                }
                is Resource.Success -> {
                    showLoading(false)
                    viewModel.allPetani.value = Resource.Success(it.data ?: mutableListOf())
                }
                is Resource.Error -> {
                    showLoading(false)
                    viewModel.allPetani.value = Resource.Error(it.message.toString())
                }
            }
        }
    }

    private fun observeKebun(filter: String = "") {
        viewModel.getAllKebun(filter).observe(this) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                    viewModel.allKebun.value = Resource.Loading()
                }
                is Resource.Success -> {
                    showLoading(false)
                    viewModel.allKebun.value = Resource.Success(it.data ?: mutableListOf())
                }
                is Resource.Error -> {
                    showLoading(false)
                    viewModel.allKebun.value = Resource.Error(it.message.toString())
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuItem = menu?.findItem(R.id.action_list)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.item_serach)

        val unwrappedDrawable: Drawable? = searchItem?.icon
        val wrappedDrawable = unwrappedDrawable?.let { DrawableCompat.wrap(it) }
        if (wrappedDrawable != null) {
            DrawableCompat.setTint(
                wrappedDrawable,
                getColor(R.color.green)
            )
        }

        searchView = searchItem?.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (listType == ListType.PETANI) {
                    newText?.trim()?.let {observePetani(it)}
                } else if (listType == ListType.KEBUN) {
                    newText?.trim()?.let {observeKebun(it) }
                }

                return true
            }
        })

        return super.onPrepareOptionsMenu(menu)
    }

    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility =
            if (isShowLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_petani_kebun, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_list -> {
                when (listType) {
                    ListType.PETANI -> {
                        searchView.setQuery("", false)
                        searchView.clearFocus()
                        binding.frameContainer.findNavController()
                            .navigate(R.id.action_listPetaniFragment2_to_listKebunFragment)
                        listType = ListType.KEBUN
                        binding.toolbarTitle.text = getString(R.string.kebun)
                        menuItem?.let { menuItem!!.setIcon(R.drawable.icon_list_petani) }


                    }
                    ListType.KEBUN -> {
                        searchView.setQuery("", false)
                        searchView.clearFocus()
                        binding.frameContainer.findNavController()
                            .navigate(R.id.action_listKebunFragment_to_listPetaniFragment2)
                        listType = ListType.PETANI
                        binding.toolbarTitle.text = getString(R.string.petani)
                        menuItem?.let { menuItem!!.setIcon(R.drawable.icon_list_kebun) }
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}