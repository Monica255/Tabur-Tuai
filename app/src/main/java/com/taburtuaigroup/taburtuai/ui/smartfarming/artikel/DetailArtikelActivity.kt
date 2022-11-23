package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.databinding.DetailArtikelBinding
import com.taburtuaigroup.taburtuai.util.*

class DetailArtikelActivity  : AppCompatActivity() {
    private var _binding: DetailArtikelBinding? = null
    private val binding get() = _binding!!
    private lateinit var artikelId:String
    private lateinit var viewModel:DetailArtikelViewModel
    private var data=Artikel()
    private val uid=FirebaseAuth.getInstance().currentUser?.uid
    private var sendReslt=false
    override fun onDestroy() {
        binding.cbFav.isChecked=false
        _binding=null
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= DetailArtikelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(application))[DetailArtikelViewModel::class.java]

        artikelId = intent.getStringExtra(ARTIKEL_ID) ?: ""
        viewModel.artikelId = artikelId

        viewModel.artikel.observe(this){itt->
            data=itt
            setData(data)
            binding.cbFav.setOnClickListener {
                viewModel.favoriteArtikel(data)
            }
        }

        viewModel.isFav.observe(this){
            val x=it.getContentIfNotHandled()
            if (x==true){
                uid?.let { it1 -> data.favorites?.add(it1) }
            }else if(x==false){
                data.favorites?.remove(uid)
            }
        }

        viewModel.mesage.observe(this){
            ToastUtil.showSnackbar(binding.root,it)
            if(it.first)binding.cbFav.isChecked=isFavorite(data.favorites)
            sendReslt = !it.first
        }
    }

    private fun isFavorite(list:List<String>?):Boolean{
        var x=false
        if(list!=null){
            x=list.contains(uid)
        }
        return x
    }

    private fun setData(artikel:Artikel){
        Glide.with(this)
            .load(artikel.img_header)
            .placeholder(R.drawable.placeholder)
            .into(binding.imgHeaderArtikel)

        binding.apply {
            tvTitleArtikel.text=artikel.title
            tvDate.text=getString(R.string.dipublikasikan_pada,DateConverter.convertMillisToDate(artikel.timestamp,this@DetailArtikelActivity))
            tvAuthor.text=if(artikel.author.trim()!="")getString(R.string.oleh,TextFormater.toTitleCase(artikel.author)) else ""
            tvArtikel.text=artikel.artikel_text.replace("\\n", "\n")
            tvKategoriArtikel.text=when(artikel.kategori.trim()){
                "informasi"->resources.getString(R.string.informasi)
                "edukasi"->resources.getString(R.string.edukasi)
                else->resources.getString(R.string.lainnya)
            }
            cbFav.isChecked=isFavorite(artikel.favorites)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if(sendReslt){
            val intent = Intent()
            intent.putExtra(ARTIKEL, data)
            setResult(RESULT_OK, intent)
        }
        super.onBackPressed()
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }
}