package com.taburtuaigroup.taburtuai.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.UserData
import com.taburtuaigroup.taburtuai.core.features.scheduler.Scheduler
import com.taburtuaigroup.taburtuai.databinding.ActivityProfileBinding
import com.taburtuaigroup.taburtuai.ui.WelcomePageActivity
import com.taburtuaigroup.taburtuai.ui.feedback.FeedbackActivity
import com.taburtuaigroup.taburtuai.ui.listpetanikebun.PetaniKebunActivity
import com.taburtuaigroup.taburtuai.core.util.IS_ALWAYS_LOGIN_PETANI
import com.taburtuaigroup.taburtuai.core.util.SESI_PETANI_ID
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityProfileBinding
    private val viewModel:ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        val prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val isAlwayLogin=prefManager.getBoolean(IS_ALWAYS_LOGIN_PETANI,false)

        binding.swSelaluMasukPetani.isChecked=isAlwayLogin

        setAction()

        viewModel.userData.observe(this){
            if(it!=null)setData(it)
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

    private fun setAction(){
        binding.btGantiBahasa.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.swSelaluMasukPetani.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSelaluLoginPetani(isChecked)
        }

        binding.btKeluar.setOnClickListener{
            showConfirmDialog()
        }

        binding.cvPetaniKebun.setOnClickListener {
            startActivity(Intent(this,PetaniKebunActivity::class.java))
        }

        binding.clDropALine.setOnClickListener {
            startActivity(Intent(this,FeedbackActivity::class.java))
        }

        binding.cvProfile.setOnClickListener {
            startActivity(Intent(this,EditProfileActivity::class.java))
        }

    }

    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(this)
        val mConfirmDialog = builder.create()
        builder.setTitle(getString(R.string.keluar))
        builder.setMessage(getString(R.string.yakin_ingin_keluar))
        builder.create()

        builder.setPositiveButton(getString(R.string.ya)) { _, _ ->
            viewModel.getAllActiveScheduler().observe(this){
                when(it){
                    is Resource.Loading ->{
                        showLoading(true)
                    }
                    is Resource.Error->{
                        showLoading(false)
                        ToastUtil.makeToast(this,it.message.toString())
                    }
                    is Resource.Success->{
                        it.data?.let {
                            val sche= Scheduler()
                            sche.cancelAllAlarm(this,it)
                            viewModel.signOut()
                            val intent = Intent(this, WelcomePageActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            showLoading(false)
                        }

                    }
                }
            }

        }

        builder.setNegativeButton(getString(R.string.tidak)) { _, _ ->
            mConfirmDialog.cancel()
        }
        builder.show()
    }

    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility =
            if (isShowLoading) View.VISIBLE else View.GONE
    }

    private fun setData(it: UserData) {
        val prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val alwaysLogin = prefManager.getBoolean(IS_ALWAYS_LOGIN_PETANI, false)
        binding.apply {
            Glide.with(binding.root)
                .load(it.img_profile)
                .placeholder(R.drawable.img_placeholder_profile)
                .into(binding.imgProfile)

            tvName.text=it.name
            tvEmail.text=it.email
            tvPhone.text=if(it.phone_number=="") "-" else it.phone_number
            swSelaluMasukPetani.isChecked=alwaysLogin
        }
    }
}