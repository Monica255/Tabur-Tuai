package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.penjadwalan

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Device
import com.taburtuaigroup.taburtuai.core.domain.model.Kebun
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.databinding.FragmentPilihDeviceBinding
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun.RealtimeAdapter
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.pilihkebun.KebunAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PilihDeviceFragment : DialogFragment() {
    private var _binding: FragmentPilihDeviceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateScheduleViewModel by activityViewModels()
    private var idKebun: String? = null
    private lateinit var adapter: RealtimeAdapter
    private lateinit var onGetData: OnGetDataDevice

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onGetData = activity as OnGetDataDevice
        } catch (e: ClassCastException) {
            Log.e(
                "TAG", "onAttach: ClassCastException: "
                        + e.message
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvKebun.layoutManager = layoutManager

        adapter = RealtimeAdapter { device ->
            Log.d("TAG","dev "+device)
            onGetData.handleData(device)
            dismiss()
        }
        binding.rvKebun.adapter = adapter

        viewModel.isConnected.observe(requireActivity()) {
            adapter.isConnected = it
        }

        viewModel.kebun.observe(this){
            if (it != null) {
                binding.toolbarTitle.text = it.nama_kebun
                idKebun = it.id_kebun
                viewModel.getAllDevice(idKebun = it.id_kebun).observe(this) {
                    handleDataDevice(it)
                }

            } else {
                dismiss()
            }

        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun handleDataDevice(data: Resource<List<Device>>) {
        when (data) {
            is Resource.Loading -> showLoading(true)
            is Resource.Success -> {
                showLoading(false)
                val list = data.data
                list?.let { showRecyclerView(it) }
            }
            is Resource.Error -> {
                showLoading(false)
                data.message?.let { ToastUtil.makeToast(requireContext(), it) }
            }
        }
    }

    private fun showLoading(it: Boolean) {
        binding.pbLoading.visibility = if (it) View.VISIBLE else View.GONE
    }
    private fun showRecyclerView(device: List<Device>) {
        adapter.submitList(device.toMutableList())
        adapter.notifyDataSetChanged()
        binding.rvKebun.visibility = if (device.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentPilihDeviceBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
interface OnGetDataDevice {
    fun handleData(data: Device?)
}