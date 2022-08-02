package com.example.taburtuai.ui.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.taburtuai.R
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.data.Masukan
import com.example.taburtuai.databinding.ActivityFeedbackBinding
import com.example.taburtuai.util.*
import com.google.firebase.auth.FirebaseAuth

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFeedbackBinding
    private lateinit var viewModel:FeedbackViewModel
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
    private var errorMsg:Event<String>?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val pref=androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val count=pref.getInt(FEEDBACK_COUNT,0)
        Log.d("TAG",count.toString())

        viewModel=ViewModelProvider(
            this,ViewModelFactory.getInstance(application)
        )[FeedbackViewModel::class.java]

        binding.btKirim.setOnClickListener {
            if(isDataValid()){
                if(viewModel.isCanSendFeedBack){
                    val email=if(!binding.cbAnonim.isChecked)FirebaseAuth.getInstance().currentUser?.email else "anonim"
                    val data=Masukan(
                        jenisMasukan,masukan,
                        email?:"anonim",
                        DateConverter.convertMillisToString(System.currentTimeMillis())
                    )

                    viewModel.kirimMasukan(data)
                }else{
                   errorMsg=Event("Ups jangan spam ya\nSilahkan kirim masukan lagi besok")
                    showToast()
                }
            }else if(!isFieldsEmpty()){
                showToast()
            }
        }


        viewModel.isLoading.observe(this, Observer(this::showLoading))

        viewModel.message.observe(this){
            makeToast(it)
        }

        viewModel.isConnected.observe(this) {
            ToastUtil.showInternetSnackbar(
                this,
                binding.root,
                it
            )
        }

    }

    private fun makeToast(pair: Pair<Boolean, Event<String>>) {
        val msg = pair.second.getContentIfNotHandled()
        if (msg != null) {
            if (!pair.first) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                //TODO show error toast
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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