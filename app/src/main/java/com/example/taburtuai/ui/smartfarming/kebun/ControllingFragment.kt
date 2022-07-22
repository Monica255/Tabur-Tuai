package com.example.taburtuai.ui.smartfarming.kebun

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.data.Device
import com.example.taburtuai.data.RealtimeKebun
import com.example.taburtuai.databinding.FragmentControllingBinding
import com.google.android.material.snackbar.Snackbar


class ControllingFragment : Fragment() {
    private var _binding: FragmentControllingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: KebunViewModel
    private var isConnected=false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory.getInstance(requireActivity())
        )[KebunViewModel::class.java]

        val layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvControlling.layoutManager = layoutManager
        viewModel.controlling.observe(requireActivity()) {
            if (it != null) showRecyclerView(it)
            else {
                //TODO show no data
            }

        }


    }

    private fun showRecyclerView(data: List<Device>) {

        if (data?.isNotEmpty()) {

            val taskAdapter = RealtimeAdapter { device ->
                viewModel.updateDeviceState(
                    viewModel.kebunId, device.id_device,
                    if (device.state == 1) 0 else 1
                )
            }
            viewModel.isConnected.observe(requireActivity()) {
                taskAdapter.isConnected=it
            }
            taskAdapter.submitList(data)
            binding.rvControlling.adapter = taskAdapter
        } else {
            //TODO show user no data
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentControllingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}