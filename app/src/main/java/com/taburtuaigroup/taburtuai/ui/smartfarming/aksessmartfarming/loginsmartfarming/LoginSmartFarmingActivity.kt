package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.loginsmartfarming

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Petani
import com.taburtuaigroup.taburtuai.databinding.ActivityLoginSmartFarmingBinding
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.pilihkebun.PilihKebunActivity
import com.taburtuaigroup.taburtuai.core.util.EXTRA_PETANI
import com.taburtuaigroup.taburtuai.core.util.LoadingUtils
import com.taburtuaigroup.taburtuai.core.util.SESI_PETANI_ID
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginSmartFarmingActivity : AppCompatActivity() {
    private val viewModel: LoginSmartFarmingViewModel by viewModels()
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


        binding.btMasuk.setOnClickListener {
            if (!isFieldEmpty()) {
                lifecycleScope.launch {
                    viewModel.loginPetani(id, pass).observe(this@LoginSmartFarmingActivity) {
                        when (it) {
                            is Resource.Loading -> {
                                showLoading(true)
                            }
                            is Resource.Success -> {
                                showLoading(false)
                                it.data?.let {
                                    val prefManager =
                                        androidx.preference.PreferenceManager.getDefaultSharedPreferences(
                                            this@LoginSmartFarmingActivity
                                        )
                                    prefManager.edit().putString(SESI_PETANI_ID, it?.id_petani)
                                        .apply()
                                    goToPetaniProfile(it)
                                    finish()
                                }

                            }
                            is Resource.Error -> {
                                ToastUtil.makeToast(
                                    this@LoginSmartFarmingActivity,
                                    it.message.toString()
                                )
                                showLoading(false)
                            }
                        }
                    }
                }

            }

        }
    }

    private fun goToPetaniProfile(petani: Petani) {
        startActivity(
            Intent(
                this,
                PilihKebunActivity::class.java
            ).putExtra(EXTRA_PETANI, petani)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        )
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