package com.taburtuaigroup.taburtuai.ui.smartfarming.fav

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.databinding.ActivityFavBinding
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.PagingArtikelAdapter
import com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan.PagingPenyakitTumbuhanAdapter
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.DetailArtikelActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan.DetailPenyakitActivity
import com.taburtuaigroup.taburtuai.util.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavBinding
    private var kategoriFav: Kategori = Kategori.ARTIKEL
    private lateinit var adapterArtikel: PagingArtikelAdapter
    private lateinit var adapterPenyakit: PagingPenyakitTumbuhanAdapter
    private lateinit var viewModel: FavViewModel
    private val uid=FirebaseAuth.getInstance().currentUser?.uid
    private var tempArtikel:Artikel?=null
    private var tempPenyakit:PenyakitTumbuhan?=null

    private val onClickPenyakit:((PenyakitTumbuhan)->Unit)={ penyakit ->
        val intent = Intent(this, DetailPenyakitActivity::class.java)
        intent.putExtra(PENYAKIT_ID, penyakit.id_penyakit)
        launcherForResult.launch(intent)
    }

    private val onCheckChangedPenyakit:((PenyakitTumbuhan)->Unit)={ penyakit ->
        viewModel.favoritePenyakit(penyakit)
        tempPenyakit=penyakit
        //if(!favorite)viewModel.onViewEvent(ViewEventsPenyakit.Remove(penyakit))
    }

    private val onCLickArtikel:((Artikel)->Unit)={ artikel ->
        val intent = Intent(this, DetailArtikelActivity::class.java)
        intent.putExtra(ARTIKEL_ID, artikel.id_artikel)
        launcherForResult.launch(intent)
    }

    private val onCheckChangedArtikel:((Artikel)->Unit)={ artikel ->
        viewModel.favoriteArtikel(artikel)
        tempArtikel=artikel
        //if(!favorite)viewModel.onViewEvent(ViewEventsArtikel.Remove(artikel))

    }
    private val launcherForResult= registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultArtikel = result.data?.getParcelableExtra<Artikel>(ARTIKEL)
            val resultPenyakit =result.data?.getParcelableExtra<PenyakitTumbuhan>(PENYAKIT_TUMBUHAN)
            if(!isFavorite(resultArtikel?.favorites)&&resultArtikel!=null) viewModel.onViewEvent(ViewEventsArtikel.Remove(resultArtikel))
            if(!isFavorite(resultPenyakit?.favorites)&&resultPenyakit!=null) viewModel.onViewEvent(ViewEventsPenyakit.Remove(resultPenyakit))
        }
    }
    private fun isFavorite(list:List<String>?):Boolean{
        var isfav=false
        if(list!=null){
            isfav=list.contains(uid)
        }
        return isfav
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        setAction()


        binding.rgKategori.check(R.id.rb_kategori_artikel)

        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory.getInstance(application)
            )[FavViewModel::class.java]

        val layoutManagerPenyakit = LinearLayoutManager(this)
        binding.rvPenyakit.layoutManager = layoutManagerPenyakit

        val layoutManagerArtikel = LinearLayoutManager(this)
        binding.rvArtikel.layoutManager = layoutManagerArtikel


        adapterPenyakit = PagingPenyakitTumbuhanAdapter(onClickPenyakit,onCheckChangedPenyakit)

        adapterArtikel = PagingArtikelAdapter(onCLickArtikel,onCheckChangedArtikel)

        binding.rvPenyakit.adapter = adapterPenyakit
        binding.rvArtikel.adapter = adapterArtikel

        lifecycleScope.launch {
            adapterArtikel.loadStateFlow.collectLatest { loadStates ->
                showLoading(loadStates.refresh is LoadState.Loading)
            }
        }
        lifecycleScope.launch {
            adapterPenyakit.loadStateFlow.collectLatest { loadStates ->
                showLoading(loadStates.refresh is LoadState.Loading)
            }
        }


        viewModel.messsage.observe(this){
            ToastUtil.showSnackbar(binding.root,it)
            if(!it.first){
                if(tempArtikel!=null)viewModel.onViewEvent(ViewEventsArtikel.Remove(tempArtikel!!));tempArtikel=null
                if(tempPenyakit!=null)viewModel.onViewEvent(ViewEventsPenyakit.Remove(tempPenyakit!!));tempPenyakit=null
            }
        }

        viewModel.pagingArtikelViewStates.observe(this){
            adapterArtikel.submitData(lifecycle,it)
        }

        viewModel.pagingPenyakitViewStates.observe(this){
            adapterPenyakit.submitData(lifecycle,it)
        }

    }


    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility=if(isShowLoading) View.VISIBLE else View.GONE
    }

    private fun setAction() {
        binding.rgKategori.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rb_kategori_artikel -> {
                    showRv(Kategori.ARTIKEL)
                }
                R.id.rb_kategori_penyakit -> {
                    showRv(Kategori.PENYAKIT)
                }
            }

        }

    }

    private fun showRv(kat: Kategori) {
        when (kat) {
            Kategori.ARTIKEL -> {
                binding.rvArtikel.visibility = View.VISIBLE
                binding.rvPenyakit.visibility = View.GONE
                kategoriFav = Kategori.ARTIKEL
            }
            Kategori.PENYAKIT -> {
                binding.rvArtikel.visibility = View.GONE
                binding.rvPenyakit.visibility = View.VISIBLE
                kategoriFav = Kategori.PENYAKIT
            }
        }
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