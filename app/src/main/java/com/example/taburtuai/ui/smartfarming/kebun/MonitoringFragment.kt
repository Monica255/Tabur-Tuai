package com.example.taburtuai.ui.smartfarming.kebun

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.data.MonitoringKebun
import com.example.taburtuai.data.RealtimeKebun
import com.example.taburtuai.databinding.FragmentMonitoringBinding
import com.example.taburtuai.util.TextFormater


class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: KebunViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory.getInstance(requireActivity())
        )[KebunViewModel::class.java]

        viewModel.monitoring.observe(requireActivity()) {
            Log.d("TAG","data "+it.toString())
            if (it != null) {
                showData(it)
            }
            else {
                //TODO show no data
            }

        }

    }

    private fun showData(data: MonitoringKebun) {
        binding.apply {
            tvTemperature.text = data?.temperatur.toString()
            tvArahAngin.text = TextFormater.toTitleCase(data?.arah_angin.toString())
            tvKecepatanAir.text = data?.kecepatan_air.toString()
            tvKelembaban.text = data?.kelembaban_tanah.toString()
            tvPhTanah.text = data?.ph_tanah.toString()
            tvKecepatanAngin.text = data?.kecepatan_angin.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}