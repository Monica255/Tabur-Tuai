@file:Suppress("NAME_SHADOWING")

package com.taburtuaigroup.taburtuai.core.googleassistant


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.util.DEVICE_NAME
import com.taburtuaigroup.taburtuai.core.util.FARM_NAME
import com.taburtuaigroup.taburtuai.core.util.STATUS
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.googleassistant.GoogleAssistantResponseFragment.STATUS.*
import com.taburtuaigroup.taburtuai.databinding.FragmentGoogleAssistantResponseBinding
import com.taburtuaigroup.taburtuai.core.domain.model.Device
import com.taburtuaigroup.taburtuai.ui.home.HomeViewModel


class GoogleAssistantResponseFragment : DialogFragment() {

    private var _binding: FragmentGoogleAssistantResponseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val status = arguments?.getString(STATUS)
        val farmName = arguments?.getString(FARM_NAME)
        val deviceName = arguments?.getString(DEVICE_NAME)


        viewModel.isConnected.observe(requireActivity()) { it ->
            if (it) {
                if (status != null && farmName != null && deviceName != null) {
                    val state = when (status) {
                        "nyalakan" -> 1
                        "matikan" -> 0
                        else -> 2
                    }
                    val idKebun = farmName.replace(" ", "_")
                    val idDevice = deviceName.replace(" ", "_")

                    binding.tvCommand.text=getString(R.string.command,if(state==1)"Menyalakan" else "Mematikan",idDevice,idKebun)
                    viewModel.getControllingKebun(idKebun).observe(requireActivity()) { it->
                        when (it) {
                            is Resource.Loading -> {
                                showStatus(LOADING)
                            }
                            is Resource.Success -> {
                                it.data?.let {it->
                                    var device: Device? = null
                                    it.forEach { dv ->
                                        if (dv.id_device == idDevice) {
                                            device = dv
                                            when (state) {
                                                0 -> {
                                                    when (dv.state) {
                                                        1 -> {
                                                            viewModel.updateDeviceState(
                                                                idKebun,
                                                                idDevice,
                                                                0
                                                            )
                                                        }
                                                        0 -> {
                                                            binding.tvCommand.text=getString(R.string.device_dinyalakan,idDevice,idKebun)
                                                            showStatus(SUCESS)
                                                        }
                                                    }
                                                }
                                                1 -> {
                                                    when (dv.state) {
                                                        1 -> {
                                                            binding.tvCommand.text=getString(R.string.device_dimatikan,idDevice,idKebun)
                                                            showStatus(SUCESS)
                                                        }
                                                        0 -> {
                                                            viewModel.updateDeviceState(
                                                                idKebun,
                                                                idDevice,
                                                                1
                                                            )
                                                        }
                                                    }
                                                }
                                                else -> {}
                                            }
                                        }
                                    }
                                    if (device == null) {
                                        binding.tvCommand.text=getString(R.string.alat_tidak_ditemukan)
                                        showStatus(FAILED)
                                    }
                                }
                            }
                            is Resource.Error -> {
                                binding.tvCommand.text=getString(R.string.kebun_tidak_ditemukan)
                                showStatus(FAILED)
                            }
                        }
                    }

                } else {
                    ToastUtil.makeToast(requireContext(), getString(R.string.perintah_invalid))
                    dismiss()
                }
            }
        }
    }

    private fun showStatus(status:STATUS) {
        when(status){
            LOADING->{
                binding.progressBar.visibility=View.VISIBLE
                binding.ltJavrvisFailed.visibility=View.GONE
                binding.ltJavrvisSuccess.visibility=View.GONE
            }
            SUCESS->{
                binding.progressBar.visibility=View.GONE
                binding.ltJavrvisFailed.visibility=View.GONE
                binding.ltJavrvisSuccess.visibility=View.VISIBLE
            }
            FAILED->{
                binding.progressBar.visibility=View.GONE
                binding.ltJavrvisFailed.visibility=View.VISIBLE
                binding.ltJavrvisSuccess.visibility=View.GONE
            }
        }
    }

    enum class STATUS{
        LOADING,SUCESS,FAILED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoogleAssistantResponseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}