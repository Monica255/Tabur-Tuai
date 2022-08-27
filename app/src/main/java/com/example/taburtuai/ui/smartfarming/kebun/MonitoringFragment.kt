package com.example.taburtuai.ui.smartfarming.kebun

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taburtuai.R
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.data.MonitoringKebun
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
            ViewModelFactory.getInstance(requireActivity().application)
        )[KebunViewModel::class.java]

        viewModel.monitoring.observe(requireActivity()) {
            //Log.d("TAG","data "+it.toString())
            if(isAdded){
                if (it != null) {
                    showData(it)
                } /*else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.tidak_ada_data_monitoring),
                        Toast.LENGTH_SHORT
                    ).show()
                    //TODO show no data
                }*/
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun showData(data: MonitoringKebun) {
        binding.apply {
            tvTemperature.text = (data.temperatur ?:"-").toString()+" \u2103"
            tvKelembaban.text = (data.kelembaban_tanah ?:"-").toString()+" RH"
            tvPhTanah.text = "pH "+(data.ph_tanah ?:"-").toString()
            tvKelembabanUdara.text = (data.humidity ?:"-").toString()+" g/"+"\u33A5"
            tvArahAngin.text = if(data.arah_angin !="") TextFormater.toTitleCase(data.arah_angin) else "-"
            tvKecepatanAngin.text = (data.kecepatan_angin ?:"-").toString()+" km/H"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }
}