package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Device
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.databinding.FragmentControllingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ControllingFragment : Fragment() {
    private var _binding: FragmentControllingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: KebunViewModel by activityViewModels()
    private lateinit var taskAdapter: RealtimeAdapter
    private var list= mutableListOf<Device>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvControlling.setHasFixedSize(true)

        val layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvControlling.layoutManager = layoutManager

        taskAdapter = RealtimeAdapter { device ->
            viewModel.updateDeviceState(
                viewModel.kebunId, device.id_device,
                if (device.state == 1) 0 else 1
            ).observe(requireActivity()){
                when(it){
                    is Resource.Error->{
                        it?.data?.let{
                            ToastUtil.makeToast(requireActivity(),it)
                        }
                    }
                    is Resource.Success->{

                    }
                }
            }
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