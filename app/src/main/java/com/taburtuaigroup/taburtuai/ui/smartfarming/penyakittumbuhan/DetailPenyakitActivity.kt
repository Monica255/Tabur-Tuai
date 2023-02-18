package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.util.*
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.databinding.ActivityDetailPenyakitBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailPenyakitActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailPenyakitBinding
    private val viewModel:DetailPenyakitViewModel by viewModels()
    private var penyakitId=""
    private var data= PenyakitTumbuhan()
    private val uid=FirebaseAuth.getInstance().currentUser?.uid
    private var sendReslt=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailPenyakitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        penyakitId = intent.getStringExtra(PENYAKIT_ID) ?: ""

        viewModel.getPenyakit(penyakitId).observe(this){ it ->
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    it.data?.let { it ->
                        data = it
                        setData(data)
                        binding.cbFav.setOnClickListener {
                            viewModel.favoritePenyakit(data).observe(this){ it ->
                                when(it){
                                    is Resource.Loading->{}
                                    is Resource.Success->{
                                        sendReslt = true
                                        it.data?.let {
                                            val msg=if (it.first) "Penyakit tumbuhan disimpan di list disukai" else "Penyakit tumbuhan dihapus dari list disukai"
                                            ToastUtil.makeSnackbar(binding.root, msg)
                                            if (it.first) {
                                                it.second?.let { it1 -> data.favorites?.add(it1) }
                                            } else {
                                                data.favorites?.remove(it.second)
                                            }
                                        }
                                    }
                                    is Resource.Error->{
                                        sendReslt = false
                                        ToastUtil.makeSnackbar(binding.root, it.message.toString())
                                        binding.cbFav.isChecked = isFavorite(data.favorites)
                                    }
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> ToastUtil.makeToast(this, it.message.toString())
            }
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
            tvAuthor.text=if(penyakit.author.trim()!="")getString(R.string.oleh, TextFormater.toTitleCase(penyakit.author)) else ""
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