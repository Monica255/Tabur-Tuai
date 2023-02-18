package com.taburtuaigroup.taburtuai.ui.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.taburtuaigroup.taburtuai.R

import com.taburtuaigroup.taburtuai.core.domain.model.Masukan
import com.taburtuaigroup.taburtuai.databinding.ActivityFeedbackBinding
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.core.util.Event
import com.taburtuaigroup.taburtuai.core.util.LoadingUtils
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.core.data.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFeedbackBinding
    private val viewModel:FeedbackViewModel by viewModels()
    private var isJenisValid=false
    get() {
        checkJenisMasukan()
        return field
    }
    private var isMasukanValid=false
    get(){
        checkMasukan()
        return field
    }

    private var jenisMasukan=""
    private var masukan=""
    private var errorMsg: Event<String>?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        binding.btKirim.setOnClickListener {
            if(isDataValid()){
                if(viewModel.isCanSendFeedBack){
                    val email=if(!binding.cbAnonim.isChecked)FirebaseAuth.getInstance().currentUser?.email else "anonim"
                    val currentTime=System.currentTimeMillis().toString()
                    val data= Masukan(
                        jenisMasukan,masukan,
                        email?:"anonim",
                        currentTime
                    )

                    lifecycleScope.launch {
                        viewModel.kirimMasukan(data).observe(this@FeedbackActivity){ it ->
                            when(it){
                                is Resource.Loading->{
                                    showLoading(true)
                                }
                                is Resource.Success->{
                                    showLoading(false)
                                    it.data?.let { ToastUtil.makeToast(baseContext,it) }
                                    finish()
                                }
                                is Resource.Error->{
                                    showLoading(false)
                                    it.data?.let { ToastUtil.makeToast(baseContext,it) }
                                }
                            }
                        }
                    }

                }else{
                   errorMsg= Event("Ups jangan spam ya\nSilahkan kirim masukan lagi besok")
                    showToast()
                }
            }else if(!isFieldsEmpty()){
                showToast()
            }
        }

    }


    private fun isDataValid():Boolean{
        errorMsg=null
        return isJenisValid&&isMasukanValid
    }
    private fun showToast(){
        val msg=errorMsg?.getContentIfNotHandled()?:return
        Toast.makeText(this,
            msg
            , Toast.LENGTH_SHORT).show()

    }

    private fun isFieldsEmpty(): Boolean {
        return binding.etMasukan.text.toString().trim() == ""
                && binding.rgJenisMasukan.checkedRadioButtonId==-1

    }

    private fun checkJenisMasukan(){
        val selectedBloodType = binding.rgJenisMasukan.checkedRadioButtonId
        if (selectedBloodType == -1) {
            jenisMasukan=""
            isJenisValid = false
            errorMsg= Event(getString(R.string.error_msg_pilih_jenis_masukan))
        } else {
            val radio: RadioButton = findViewById(selectedBloodType)
            isJenisValid = true
            if(radio.id== R.id.rb_gangguan){
                jenisMasukan="gangguan teknis"
            }else if (radio.id==R.id.rb_ide){
                jenisMasukan="ide"
            }
        }
    }

    private fun showLoading(isLoading:Boolean){
        if(isLoading){
            LoadingUtils.showLoading(this,false)
        }else{
            LoadingUtils.hideLoading()
        }
    }

    private fun checkMasukan(){
        val masukan=binding.etMasukan.text.toString().trim()
        if(masukan.isEmpty()){
            isMasukanValid=false
            this.masukan=""
            errorMsg= Event(getString(R.string.masukan_masih_kosong))
        }else if(masukan.length>200){
            isMasukanValid=false
            this.masukan=""
            errorMsg= Event(getString(R.string.jumlah_karakter_masukan_lebih))
        }else{
            isMasukanValid=true
            this.masukan=masukan
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}