package com.example.taburtuai.ui.smartfarming.loginsmartfarming

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.databinding.ActivityLoginSmartFarmingBinding
import com.example.taburtuai.ui.smartfarming.pilihkebun.PilihKebunActivity
import com.example.taburtuai.util.LoadingUtils
import com.example.taburtuai.util.SESI_PETANI_ID
import com.example.taburtuai.util.ToastUtil

class LoginSmartFarmingActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginSmartFarmingViewModel
    private lateinit var binding: ActivityLoginSmartFarmingBinding
    private lateinit var id: String
    private lateinit var pass: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSmartFarmingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.etSfPassword.transformationMethod =
            PasswordTransformationMethod.getInstance()
        setActionBar()

        setAction()

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[LoginSmartFarmingViewModel::class.java]
        viewModel.message.value?.second?.getContentIfNotHandled()

        viewModel.petani.observe(this) {
            if (it != null) {
                val prefManager =
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
                prefManager.edit().putString(SESI_PETANI_ID, it.id_petani).apply()
                startActivity(
                    Intent(
                        this,
                        PilihKebunActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
                )
                finish()
            }
        }


        binding.btMasuk.setOnClickListener {
            if (!isFieldEmpty()) {
                viewModel.loginPetani(id, pass)
            }

        }
        viewModel.isLoading.observe(this, Observer(this::showLoading))

        viewModel.message.observe(this) {
            ToastUtil.makeToast(this, it)
        }

        viewModel.isConnected.observe(this) {
            viewModel.isConnected.value?.let {
                ToastUtil.showInternetSnackbar(
                    this,
                    binding.root,
                    it
                )
            }
        }

    }

    override fun onStart() {
        super.onStart()
        ToastUtil.sb = null
        viewModel.isConnected.value?.let {
            ToastUtil.showInternetSnackbar(
                this,
                binding.root,
                it
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            LoadingUtils.showLoading(this, false)
        } else {
            LoadingUtils.hideLoading()
        }
    }

    private fun isFieldEmpty(): Boolean {
        id = binding.etSfId.text.toString().trim()
        pass = binding.etSfPassword.text.toString().trim()
        return !(id.isNotEmpty() && pass.isNotEmpty())
    }

    private fun setAction() {
        binding.cbShowPass.setOnClickListener {
            if (binding.cbShowPass.isChecked) {
                binding.etSfPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.etSfPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
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