package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.databinding.ActivityDetailPenyakitBinding
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.DetailArtikelViewModel
import com.taburtuaigroup.taburtuai.util.*

class DetailPenyakitActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailPenyakitBinding
    private lateinit var viewModel:DetailPenyakitViewModel
    private var penyakitId=""
    private var data=PenyakitTumbuhan()
    private var isfav=false
    private val uid=FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailPenyakitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        viewModel =
            ViewModelProvider(this, ViewModelFactory.getInstance(application))[DetailPenyakitViewModel::class.java]


        penyakitId = intent.getStringExtra(PENYAKIT_ID) ?: ""
        viewModel.penyakitId = penyakitId

        viewModel.penyakit.observe(this){itt->
            data=itt
            isFavorite(data.favorites)
            setData(data)
            binding.cbFav.setOnClickListener {
                viewModel.favoritePenyakit(data)
                if (isfav){
                    data.favorites?.remove(uid)
                }else{
                    uid?.let { it1 -> data.favorites?.add(it1) }
                }
            }
        }

        viewModel.message.observe(this){
            ToastUtil.showSnackbar(binding.root,it)
        }
    }

    private fun isFavorite(list:List<String>?):Boolean{
        if(list!=null){
            isfav=list.contains(uid)
        }
        return isfav
    }

    private fun setData(penyakit: PenyakitTumbuhan){

        Glide.with(this)
            .load(penyakit.img_header)
            .placeholder(R.drawable.placeholder)
            .into(binding.imgHeaderPenyakit)

        binding.apply {
            tvTitlePenyakit.text=penyakit.title
            tvDate.text=getString(R.string.dipublikasikan_pada, DateConverter.convertMillisToDate(penyakit.timestamp,this@DetailPenyakitActivity))
            tvAuthor.text=if(penyakit.author.trim()!="")getString(R.string.oleh,TextFormater.toTitleCase(penyakit.author)) else ""
            tvDeskripsi.text=penyakit.deskripsi.replace("\\n","\n")
            tvSolusi.text=penyakit.solusi.replace("\\n","\n")
            cbFav.isChecked=isfav
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        var intent= Intent()
        intent.putExtra(PENYAKIT_TUMBUHAN,data)
        setResult(RESULT_OK,intent)
        super.onBackPressed()
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }
}