package com.taburtuaigroup.taburtuai.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.UserData
import com.taburtuaigroup.taburtuai.databinding.ActivityEditProfileBinding
import com.taburtuaigroup.taburtuai.util.Event
import com.taburtuaigroup.taburtuai.util.LoadingUtils
import com.taburtuaigroup.taburtuai.util.PROFILE_URL
import com.taburtuaigroup.taburtuai.util.ToastUtil

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: EditProfileViewModel
    private var data:UserData= UserData()
    private var telepon=""
    private var nama=""

    private lateinit var dialog: EditProfilePictureFragment
    private lateinit var arg:Bundle

    private var isNamaValid=false

    private var isTeleponValid=false


    private fun newDialog(): EditProfilePictureFragment {
        dialog = EditProfilePictureFragment()
        arg=Bundle()
        arg.putString(PROFILE_URL, data.img_profile)
        dialog.arguments = arg
        dialog.show(supportFragmentManager, "mDialog")
        viewModel.isDialogOpen=true
        return dialog
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application)
        )[EditProfileViewModel::class.java]


        binding.imgProfile.setOnClickListener {
            newDialog()
        }

        viewModel.userData.observe(this){
            if(it!=null) {
                data= UserData(it.email,it.name,it.phone_number,it.uid,it.img_profile)
                if(viewModel.newData.value==UserData()){
                    viewModel.newData.value= UserData(it.email,it.name,it.phone_number,it.uid,it.img_profile)
                }
                if(viewModel.newData.value?.img_profile!=data.img_profile){
                    viewModel.newData.value?.img_profile=data.img_profile
                }
                setData(it)
            }
        }

        binding.etName.addTextChangedListener {
            checkName()
            val newData= viewModel.newData.value
            newData?.name=binding.etName.text.toString().trim()
            viewModel.newData.value=newData
        }

        binding.etPhone.addTextChangedListener {
            checkTelepon()
            val newData= viewModel.newData.value
            newData?.phone_number=binding.etPhone.text.toString().trim()
            viewModel.newData.value=newData
        }

        viewModel.message.observe(this){
            if(!viewModel.isDialogOpen){ makeToast(it) }
        }

        viewModel.isLoading.observe(this, Observer(this::showLoading))

    }

    private fun showLoading(isLoading:Boolean){
        if(isLoading){
            LoadingUtils.showLoading(this,false)
        }else{
            LoadingUtils.hideLoading()
        }
    }

    private fun makeToast(pair: Pair<Boolean, Event<String>>) {
        val msg = pair.second.getContentIfNotHandled()
        if (msg != null) {
            if (!pair.first) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                //finish()
            } else {
                //TODO show error toast
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_profile, menu)
        val item=menu.findItem(R.id.action_save)

        viewModel.newData.observe(this){
            item.isVisible = !(isDataSame(it)||!isNamaValid||!isTeleponValid)
        }

        return true
    }
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                binding.root.hideKeyboard()
                viewModel.newData.value?.let { viewModel.updateUserData(it) }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkTelepon() {
        telepon = binding.etPhone.text.toString().trim()
        if (telepon.isEmpty()) {
            isTeleponValid = false
            binding.ilPhone.helperText=getString(R.string.lengkapi_no_telepon)
            //errorMsg = Event(R.string.phone_empty)
        } else if (telepon.length < 9) {
            isTeleponValid = false
            binding.ilPhone.helperText=getString(R.string.phone_invalid)
            //errorMsg = Event(R.string.phone_invalid)
        } else {
            binding.ilPhone.helperText=""
            isTeleponValid = true
        }
    }

    private fun checkName() {
        nama = binding.etName.text.toString().trim()
        if (nama.isEmpty()) {
            isNamaValid = false
            //errorMsg = Event(R.string.name_empty)
        } else {
            isNamaValid = true
        }
    }

    private fun isDataSame(newData:UserData):Boolean{
        return newData==data
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

    private fun setData(it: UserData) {
        binding.apply {
            Glide.with(binding.root)
                .load(it.img_profile)
                .placeholder(R.drawable.img_placeholder_profile)
                .into(binding.imgProfile)

            etName.setText(it.name)
            etEmail.setText(it.email)
            etPhone.setText(it.phone_number)
        }
    }
}