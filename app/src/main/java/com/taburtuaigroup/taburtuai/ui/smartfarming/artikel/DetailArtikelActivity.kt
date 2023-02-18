@file:Suppress("NAME_SHADOWING")

package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.util.*
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Artikel
import com.taburtuaigroup.taburtuai.databinding.DetailArtikelBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailArtikelActivity : AppCompatActivity() {
    private var _binding: DetailArtikelBinding? = null
    private val binding get() = _binding!!
    private lateinit var artikelId: String
    private val viewModel: DetailArtikelViewModel by viewModels()
    private var data = Artikel()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid
    private var sendReslt = false
    override fun onDestroy() {
        binding.cbFav.isChecked = false
        _binding = null
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DetailArtikelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()


        artikelId = intent.getStringExtra(ARTIKEL_ID) ?: ""

        viewModel.getArtikel(artikelId).observe(this) { it ->
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    it.data?.let { it ->
                        data = it
                        setData(data)
                        binding.cbFav.setOnClickListener {
                            viewModel.favoriteArtikel(data).observe(this){ it ->
                                when(it){
                                    is Resource.Loading->{}
                                    is Resource.Success->{
                                        it.data?.let {
                                            sendReslt = true
                                            val msg=if (it.first) "Artikel disimpan di list disukai" else "Artikel dihapus dari list disukai"
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

    private fun isFavorite(list: List<String>?): Boolean {
        var x = false
        if (list != null) {
            x = list.contains(uid)
        }
        return x
    }

    private fun setData(artikel: Artikel) {
        Glide.with(this)
            .load(artikel.img_header)
            .placeholder(R.drawable.placeholder)
            .into(binding.imgHeaderArtikel)

        binding.apply {
            tvTitleArtikel.text = artikel.title
            tvDate.text = getString(
                R.string.dipublikasikan_pada,
                DateConverter.convertMillisToDate(artikel.timestamp, this@DetailArtikelActivity)
            )
            tvAuthor.text = if (artikel.author.trim() != "") getString(
                R.string.oleh,
                TextFormater.toTitleCase(artikel.author)
            ) else ""
            tvArtikel.text = artikel.artikel_text.replace("\\n", "\n")
            tvKategoriArtikel.text = when (artikel.kategori.trim()) {
                "informasi" -> resources.getString(R.string.informasi)
                "edukasi" -> resources.getString(R.string.edukasi)
                else -> resources.getString(R.string.lainnya)
            }
            cbFav.isChecked = isFavorite(artikel.favorites)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (sendReslt) {
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