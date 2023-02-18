package com.taburtuaigroup.taburtuai.ui.home


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.util.*
import com.taburtuaigroup.taburtuai.core.googleassistant.GoogleAssistantResponseFragment
import com.taburtuaigroup.taburtuai.databinding.ActivityHomeBinding
import com.taburtuaigroup.taburtuai.core.domain.model.UserData
import com.taburtuaigroup.taburtuai.ui.feedback.FeedbackActivity
import com.taburtuaigroup.taburtuai.ui.profile.ProfileActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.SmartFarmingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private var data: UserData = UserData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()

        intent.handleIntent()
        binding.btSmartFarming.setOnClickListener {
            startActivity(Intent(this, SmartFarmingActivity::class.java))
        }

        viewModel.userData.observe(this) {
            if (it != null) setData(it)
        }

        binding.imgProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setData(data: UserData) {
        this.data = data
        /*val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(data?.name)
            .setPhotoUri(Uri.parse(data?.img_profile))
            .build()
        FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)*/
        binding.tvHalo.text =
            getString(R.string.halo, data.name ?: "")
        val uri: Uri? =
            if (data.img_profile.isNotEmpty()) Uri.parse(data.img_profile) else null
        setDrawable(uri)

    }

    private fun setDrawable(uri: Uri?) {
        if (uri != null) {
            Glide.with(binding.root)
                .load(uri)
                .placeholder(R.drawable.placeholder)
                .into(binding.imgProfile)
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
    }

    private fun Intent.handleIntent() {
        when (action) {
            Intent.ACTION_VIEW -> handleDeepLink(data)
            //else -> goToDefaultView()
        }
    }

    private fun handleDeepLink(data: Uri?) {
        when (data?.path) {
            "/open" -> {
                val featureType = data.getQueryParameter("featureName").orEmpty()
                navigateToActivity(featureType)
            }
            "/set" -> {
                val status = data.getQueryParameter(STATUS).orEmpty().trim().lowercase()
                val farmName = data.getQueryParameter(FARM_NAME).orEmpty().trim().lowercase()
                val deviceName = data.getQueryParameter(DEVICE_NAME).orEmpty().trim().lowercase()

                val prefManager =
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
                val petaniId = prefManager.getString(SESI_PETANI_ID, "")

                if (checkInputData(status, deviceName, farmName)) {
                    if(petaniId!=""&&petaniId!=null){
                        val bundle = Bundle()
                        bundle.putString(STATUS, status)
                        bundle.putString(FARM_NAME, farmName)
                        bundle.putString(DEVICE_NAME, deviceName)

                        val fragment = GoogleAssistantResponseFragment()
                        fragment.arguments = bundle
                        fragment.show(supportFragmentManager, "google assistant")
                    }else ToastUtil.makeToast(this, getString(R.string.belum_ada_sesi_petani))

                } else ToastUtil.makeToast(this, "Perintah invalid")


            }

        }
    }

    private fun navigateToActivity(featureType: String) {
        when (featureType) {
            "profile" -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("featureType", featureType)
                startActivity(intent)

            }
            "feedback" -> {
                val intent = Intent(this, FeedbackActivity::class.java)
                startActivity(intent)
            }
            "smart farming" -> {
                val intent = Intent(this, SmartFarmingActivity::class.java)
                startActivity(intent)
            }
            "smartfarming" -> {
                val intent = Intent(this, SmartFarmingActivity::class.java)
                startActivity(intent)
            }
            else -> {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun checkInputData(status: String, deviceName: String, farmName: String): Boolean {
        val isStatusValid: Boolean = status == "nyalakan" || status == "matikan"
        val x = deviceName.split(" ")
        val isDeviceValid: Boolean = if (x.size == 2) {
            val z = x[0].trim().lowercase()
            z == "pompa" || z == "cctv" || z == "sprinkler" || z == "fogger" || z == "sirine" || z == "lampu"
        } else false
        val y = farmName.split(" ")
        val isFarmValid: Boolean = if (y.size == 2) {
            y[0].trim().lowercase() == "kebun"
        } else false

        return isStatusValid && isDeviceValid && isFarmValid
    }
}