package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.penjadwalan

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Device
import com.taburtuaigroup.taburtuai.core.domain.model.Kebun
import com.taburtuaigroup.taburtuai.core.domain.model.Mscheduler
import com.taburtuaigroup.taburtuai.core.domain.model.Petani
import com.taburtuaigroup.taburtuai.core.features.scheduler.Scheduler
import com.taburtuaigroup.taburtuai.core.util.EXTRA_PETANI
import com.taburtuaigroup.taburtuai.core.util.Event
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.databinding.ActivityCreateSchedulerBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CreateSchedulerActivity : AppCompatActivity(),OnGetData,OnGetDataDevice {
    private lateinit var binding:ActivityCreateSchedulerBinding
    private val viewModel:CreateScheduleViewModel by viewModels()
    private var errorMsg: Event<String>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityCreateSchedulerBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActionBar()
        viewModel.petani = intent.getParcelableExtra<Petani>(EXTRA_PETANI)
        binding.cvPilihKebun.setOnClickListener {
            val mFragment= PilihKebunFragment()
            mFragment.show(supportFragmentManager,"kebun_dialog")
        }


        viewModel.kebun.observe(this){
            if(it!=null){
                binding.cvPilihDevice.isClickable=true
                binding.cvPilihDevice.setCardBackgroundColor(resources.getColor(R.color.white,theme))
                binding.cvPilihDevice.setOnClickListener {
                    val mFragment= PilihDeviceFragment()
                    mFragment.show(supportFragmentManager,"device_dialog")
                }
            }else{
                binding.cvPilihDevice.isClickable=false
                binding.cvPilihDevice.setCardBackgroundColor(resources.getColor(R.color.light_grey,theme))
            }
        }
        binding.btnBuatPenjadwalan.setOnClickListener {
            createScheduler()
        }
    }

    private fun checkAction(){
        val selectedBloodType = binding.rgAction.checkedRadioButtonId
        if (selectedBloodType == -1) {
            viewModel.action=null
            errorMsg= Event(getString(R.string.error_msg_pilih_jenis_masukan))
        } else {
            val radio: RadioButton = findViewById(selectedBloodType)
            if(radio.id== R.id.rb_mati){
                viewModel.action=0
            }else if (radio.id==R.id.rb_menyala){
                viewModel.action=1
            }
        }
    }

    private fun checkTime(){
        val x=binding.timePicker.getCurrentlySelectedTime()
        val y=x.split(":")
        if(y[0].trim()!="24"){
            viewModel.hour=y[0].trim().replaceFirst("^0+(?!$)", "").toInt()
            viewModel.minute=y[1].trim().replaceFirst("^0+(?!$)", "").toInt()
        }else {
            viewModel.hour=null
            viewModel.minute=null
        }
    }

    fun isDataValid():Boolean{
        checkAction()
        checkTime()
        return viewModel.hour!==null && viewModel.minute!=null && viewModel.petani!=null && viewModel.device !=null && viewModel.kebun.value!=null && viewModel.action !=null
    }

    private fun createScheduler(){
        if(isDataValid()){
            val calendar=Calendar.getInstance()
            val currentTime=calendar.timeInMillis.toInt()
            val mScheduler=Mscheduler(currentTime,viewModel.action!!,viewModel.kebun.value!!.id_kebun,viewModel.device!!.id_device,viewModel.petani!!.id_petani,viewModel.hour!!,viewModel.minute!!,true)
            viewModel.createSchedule(mScheduler).observe(this){
                when(it){
                    is Resource.Loading->{
                        showLoading(true)
                    }
                    is Resource.Error->{
                        showLoading(false)
                    }
                    is Resource.Success->{
                        showLoading(false)
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }
        }else{
            ToastUtil.makeToast(this,"Masukkan data dengan benar")
        }
    }

    private fun showLoading(it: Boolean) {
        binding.pbLoading.visibility = if (it) View.VISIBLE else View.GONE
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

    override fun handleData(data: Kebun?) {
        data?.let{
            viewModel.kebun.value=it
            binding.tvKebun.text=it.nama_kebun
        }
    }

    override fun handleData(data: Device?) {
        data?.let{
            viewModel.device=it
            binding.tvDevice.text=it.name
        }
    }
}