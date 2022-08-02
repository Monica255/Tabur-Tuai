package com.example.taburtuai.ui.smartfarming.kebun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.data.Device
import com.example.taburtuai.databinding.FragmentControllingBinding


class ControllingFragment : Fragment() {
    private var _binding: FragmentControllingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: KebunViewModel
    private lateinit var taskAdapter: RealtimeAdapter
    private var list= mutableListOf<Device>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory.getInstance(requireActivity().application)
        )[KebunViewModel::class.java]

        binding.rvControlling.setHasFixedSize(true)

        val layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvControlling.layoutManager = layoutManager

        taskAdapter = RealtimeAdapter { device ->
            viewModel.updateDeviceState(
                viewModel.kebunId, device.id_device,
                if (device.state == 1) 0 else 1
            )
        }
        binding.rvControlling.adapter = taskAdapter


        viewModel.isConnected.observe(requireActivity()) {
            taskAdapter.isConnected = it
        }

        viewModel.controlling.observe(requireActivity()) {
            if (it != null) showRecyclerView(it.toMutableList())
            else {
                //TODO show no data
            }

        }
    }

    private fun showRecyclerView(data: MutableList<Device>) {
        if (data.isNotEmpty()) {
            list=data
            taskAdapter.submitList(list)
        } else {
            //TODO show user no data
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentControllingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}