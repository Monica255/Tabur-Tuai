package com.taburtuaigroup.taburtuai.ui.smartfarming

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.databinding.ActivitySmartFarmingAvtivityBinding
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.loginsmartfarming.LoginSmartFarmingActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.pilihkebun.PilihKebunActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.ArtikelActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.ArtikelAdapter
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.DetailArtikelActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.fav.FavActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan.DetailPenyakitActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan.PenyakitTumbuhanActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan.PenyakitTumbuhanAdapter
import com.taburtuaigroup.taburtuai.ui.smartfarming.tutorial.CaraDaftarActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.tutorial.CaraKerjaActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.tutorial.PengenalanSmartFarmingActivity
import com.taburtuaigroup.taburtuai.util.*

class SmartFarmingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySmartFarmingAvtivityBinding
    private var list = mutableListOf<Any>()
    private lateinit var mAdapter: SliderAdapter
    private lateinit var viewModel: SmartFarmingViewModel
    private lateinit var penyakitTumbuhanAdapter: PenyakitTumbuhanAdapter
    private lateinit var artikelAdapter: ArtikelAdapter

    private var tempArtikel = mutableListOf<Artikel>()
    private var tempPenyakit = mutableListOf<PenyakitTumbuhan>()
    private val onClickPenyakit: ((PenyakitTumbuhan) -> Unit) = { penyakit ->
        val intent = Intent(this, DetailPenyakitActivity::class.java)
        intent.putExtra(PENYAKIT_ID, penyakit.id_penyakit)
        startActivity(intent)
    }
    private val onCLickArtikel: ((Artikel) -> Unit) = { artikel ->
        val intent = Intent(this, DetailArtikelActivity::class.java)
        intent.putExtra(ARTIKEL_ID, artikel.id_artikel)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmartFarmingAvtivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        setAction()

        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory.getInstance(application)
            )[SmartFarmingViewModel::class.java]

        val layoutManagerPenyakit = LinearLayoutManager(this)
        binding.rvPenyakitTumbuhan.layoutManager = layoutManagerPenyakit

        val layoutManagerArtikel = LinearLayoutManager(this)
        binding.rvArtikel.layoutManager = layoutManagerArtikel

        mAdapter = SliderAdapter { data ->
            when (data) {
                is Artikel -> {
                    val intent = Intent(this, DetailArtikelActivity::class.java)
                    intent.putExtra(ARTIKEL_ID, data.id_artikel)
                    startActivity(intent)
                }
                is PenyakitTumbuhan -> {
                    val intent = Intent(this, DetailPenyakitActivity::class.java)
                    intent.putExtra(PENYAKIT_ID, data.id_penyakit)
                    startActivity(intent)
                }
            }

        }
        artikelAdapter = ArtikelAdapter(onCLickArtikel, null)
        binding.rvArtikel.adapter = artikelAdapter
        penyakitTumbuhanAdapter = PenyakitTumbuhanAdapter(onClickPenyakit, null)
        binding.rvPenyakitTumbuhan.adapter = penyakitTumbuhanAdapter

        binding.imageSlider.apply {
            setSliderAdapter(mAdapter)
            setIndicatorAnimation(IndicatorAnimationType.WORM) //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
            autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
            indicatorSelectedColor = Color.WHITE
            indicatorUnselectedColor = Color.GRAY
            scrollTimeInSec = 3
            startAutoCycle()
        }

        viewModel.message.observe(this) {
            ToastUtil.showSnackbar(binding.root, it)
        }

        viewModel.artikel.observe(this) { artikel ->
            viewModel.penyakit.observe(this) { penyakit ->
                setSliderData(getDataSlider(artikel, penyakit).toMutableList())
                showRecyclerViewArtikel(tempArtikel)
                showRecyclerViewPenyakit(tempPenyakit)
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }


    }

    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility =
            if (isShowLoading && tempPenyakit.size == 0 && tempArtikel.size == 0) View.VISIBLE else View.GONE
    }

    private val handler = Handler(Looper.getMainLooper())
    private fun setAction() {
        binding.nsv.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY != oldScrollY) {
                binding.extFloatingActionButton.shrink()
            }

            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({
                binding.extFloatingActionButton.extend()
            }, 300)
        }

        binding.extFloatingActionButton.setOnClickListener {
            val prefManager =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
            val petaniId = prefManager.getString(SESI_PETANI_ID, "")
            val alwaysLogin = prefManager.getBoolean(IS_ALWAYS_LOGIN_PETANI, false)

            if (alwaysLogin) {
                if (petaniId != "" && petaniId != null) {
                    viewModel.autoLoginPetani(petaniId)
                    startActivity(Intent(this, PilihKebunActivity::class.java))
                } else {
                    Toast.makeText(this, "Belum ada sesi petani", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginSmartFarmingActivity::class.java))
                }
            } else {
                startActivity(Intent(this, LoginSmartFarmingActivity::class.java))
            }
        }

        binding.llArtikel.setOnClickListener {
            startActivity(Intent(this, ArtikelActivity::class.java))
        }
        binding.llPenyakitTumbuhan.setOnClickListener {
            startActivity(Intent(this, PenyakitTumbuhanActivity::class.java))
        }
        binding.cvCaraKerja.setOnClickListener {
            startActivity(Intent(this, CaraKerjaActivity::class.java))
        }
        binding.cvPengenalan.setOnClickListener {
            startActivity(Intent(this, PengenalanSmartFarmingActivity::class.java))
        }
        binding.cvCaraDaftar.setOnClickListener {
            startActivity(Intent(this, CaraDaftarActivity::class.java))
        }
    }

    private fun setSliderData(data: MutableList<Any>) {
        if (data.isNotEmpty()) {
            binding.cvImageSlider.visibility=View.VISIBLE
            list = data
            mAdapter.submitList(list)
        } else {
            binding.cvImageSlider.visibility=View.GONE
            //TODO show user no data
        }
    }

    private fun showRecyclerViewArtikel(
        artikel: MutableList<Artikel>
    ) {
        if (artikel.isNotEmpty()) {
            binding.rvArtikel.visibility = View.VISIBLE
            binding.tvLabelArtikel.visibility = View.VISIBLE
            artikelAdapter.submitList(artikel)
        } else {
            binding.rvArtikel.visibility = View.GONE
            binding.tvLabelArtikel.visibility = View.GONE
        }

    }

    private fun showRecyclerViewPenyakit(
        penyakit: MutableList<PenyakitTumbuhan>,
    ) {
        if (penyakit.isNotEmpty()) {
            binding.rvPenyakitTumbuhan.visibility = View.VISIBLE
            binding.tvLabelPenyakitTumbuhan.visibility = View.VISIBLE
            penyakitTumbuhanAdapter.submitList(penyakit)
        } else {
            binding.rvPenyakitTumbuhan.visibility = View.GONE
            binding.tvLabelPenyakitTumbuhan.visibility = View.GONE
        }
    }

    private fun getDataSlider(
        dataArtikel: List<Artikel>,
        dataPenyakit: List<PenyakitTumbuhan>
    ): List<Any> {
        val list = arrayListOf<Any>()
        tempArtikel = dataArtikel.toMutableList()
        tempPenyakit = dataPenyakit.toMutableList()
        var artikelCount = dataArtikel.size
        var penyakitCount = dataPenyakit.size
        val max = if (artikelCount >= penyakitCount) artikelCount else penyakitCount
        if (max != 0) {
            for (i in 0 until max) {
                if (max == dataArtikel.size) {
                    if (artikelCount != 0) {
                        list.add(dataArtikel[i])
                        tempArtikel.removeFirst()
                        artikelCount--
                    }
                    if (penyakitCount != 0) {
                        list.add(dataPenyakit[i])
                        tempPenyakit.removeFirst()
                        penyakitCount--
                    }
                } else {
                    if (penyakitCount != 0) {
                        list.add(dataPenyakit[i])
                        tempPenyakit.removeFirst()
                        penyakitCount--
                    }
                    if (artikelCount != 0) {
                        list.add(dataArtikel[i])
                        tempArtikel.removeFirst()
                        artikelCount--
                    }
                }
                if (list.size == if (max >= 4) 4 else max) break
            }
        }
        tempArtikel =
            tempArtikel.take(if (tempArtikel.size >= 4) 4 else tempArtikel.size).toMutableList()
        tempPenyakit =
            tempPenyakit.take(if (tempPenyakit.size >= 4) 4 else tempPenyakit.size).toMutableList()
        return list
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_smartfarming, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_my_fav -> {
                val intent = Intent(this, FavActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}