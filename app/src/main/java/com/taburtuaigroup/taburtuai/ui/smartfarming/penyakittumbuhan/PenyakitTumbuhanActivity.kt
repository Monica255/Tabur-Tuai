package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import android.app.SearchManager
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.databinding.ActivityArtikelBinding
import com.taburtuaigroup.taburtuai.databinding.ActivityPenyakitTumbuhanBinding
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.ArtikelViewModel

class PenyakitTumbuhanActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPenyakitTumbuhanBinding
    private lateinit var viewModel: PenyakitTumbuhanViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPenyakitTumbuhanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setActionBar()
        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(this.application))[PenyakitTumbuhanViewModel::class.java]
        navController = findNavController(R.id.frame_container)
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
                if(query!=null){
                    //Log.d("TAG",viewModel.currentDes)
                    if(viewModel.currentDes=="fragment_penyakit_tumbuhan"){
                        navController.navigate(R.id.action_penyakitTumbuhanFragment2_to_listPenyakitTumbuhanFragment)
                    }
                    viewModel.getData(keyword = query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!=null){
                    if(newText?.trim()?.isEmpty()){
                        if(viewModel.mKeyword!=""){
                            viewModel.getData(keyword = "")
                        }
                    }
                }
                return true
            }
        })

        return true
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