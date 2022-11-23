package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.databinding.ActivityDetailPenyakitBinding
import com.taburtuaigroup.taburtuai.util.*

class DetailPenyakitActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailPenyakitBinding
    private lateinit var viewModel:DetailPenyakitViewModel
    private var penyakitId=""
    private var data=PenyakitTumbuhan()
    private val uid=FirebaseAuth.getInstance().currentUser?.uid
    private var sendReslt=false
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
            setData(data)
            binding.cbFav.setOnClickListener {
                viewModel.favoritePenyakit(data)
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

        viewModel.message.observe(this){
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
            cbFav.isChecked=isFavorite(penyakit.favorites)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if(sendReslt){
            val intent = Intent()
            intent.putExtra(PENYAKIT_TUMBUHAN, data)
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