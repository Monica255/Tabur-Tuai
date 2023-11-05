package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.penjadwalan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.MyApplication
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Mscheduler
import com.taburtuaigroup.taburtuai.core.domain.model.Petani
import com.taburtuaigroup.taburtuai.core.features.scheduler.Scheduler
import com.taburtuaigroup.taburtuai.core.util.EXTRA_PETANI
import com.taburtuaigroup.taburtuai.core.util.SESI_PETANI_ID
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.databinding.ActivitySchedulerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SchedulerActivity : AppCompatActivity() {
    private val viewModel: SchedulerViewModel by viewModels()
    private lateinit var adapter: SchedulerAdapter
    private lateinit var binding: ActivitySchedulerBinding
    private val prefManager =
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(MyApplication.applicationContext())
    private val resultContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                val idPetani = prefManager.getString(SESI_PETANI_ID, "") ?: ""
                getScheduler(idPetani)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySchedulerBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActionBar()
        viewModel.petani = intent.getParcelableExtra<Petani>(EXTRA_PETANI)
        val layoutManager = LinearLayoutManager(this)
        binding.rvScheduelr.layoutManager = layoutManager

        adapter = SchedulerAdapter({ mScheduler ->
            showConfirmDialog(mScheduler)
        }) { mscheduler, b ->
            if(mscheduler.active!=b){
                val sche = Scheduler()
                viewModel.updateSchedule(mscheduler, b, this).observe(this) {
                    when (it) {
                        is Resource.Error -> {
                            ToastUtil.makeToast(this, it.message.toString())
                        }
                        is Resource.Success -> {
                            if (b) {
                                sche.setScheduler(listOf(mscheduler))
                                ToastUtil.makeToast(this, "Berhasil mengaktifkan penjadwalan")
                            } else {
                                sche.cancelAllAlarm(this, listOf(mscheduler))
                                ToastUtil.makeToast(this, "Berhasil menonaktifkan penjadwalan")
                            }
                        }
                    }
                }
            }


        }
        binding.rvScheduelr.adapter = adapter


        val idPetani = prefManager.getString(SESI_PETANI_ID, "") ?: ""
        getScheduler(idPetani)


        binding.fabAddScheduler.setOnClickListener {
            val intent = Intent(
                this,
                CreateSchedulerActivity::class.java
            ).putExtra(EXTRA_PETANI, viewModel.petani)
            resultContract.launch(intent)
        }
    }

    private fun getScheduler(idPetani:String){
        viewModel.getScheduler(idPetani, false).observe(this) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    it.data?.let {
                        adapter.submitList(it.toMutableList())
                        showLoading(false)
                    }
                }
                is Resource.Error -> {
                    ToastUtil.makeToast(this, it.message.toString())
                    showLoading(false)
                }
            }
        }
    }

    private fun showConfirmDialog(mScheduler: Mscheduler) {
        val builder = AlertDialog.Builder(this)
        val mConfirmDialog = builder.create()
        builder.setTitle("Hapus")
        builder.setMessage(getString(R.string.hapus_penjadwalan))
        builder.create()
        val index=adapter.list.indexOf(mScheduler)
        builder.setPositiveButton(getString(R.string.ya)) { _, _ ->
            viewModel.deleteScheduler(mScheduler).observe(this) {
                when (it) {
                    is Resource.Loading -> {
                        showLoading(true)
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        it.message?.let { it1 -> ToastUtil.makeToast(this, it1) }
                    }
                    is Resource.Success -> {
                        if(mScheduler.active){
                            val sche=Scheduler()
                            sche.cancelAllAlarm(this, listOf(mScheduler))
                        }
                        showLoading(false)
                        adapter.deleteScheduler(index)
                    }
                }
            }
        }

        builder.setNegativeButton(getString(R.string.tidak)) { _, _ ->
            mConfirmDialog.cancel()
        }
        builder.show()
    }

    private fun showLoading(isShow: Boolean) {
        binding.pbLoading.visibility = if (isShow) View.VISIBLE else View.GONE
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