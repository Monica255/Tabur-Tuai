package com.example.taburtuai.ui.listpetanikebun

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.databinding.FragmentListKebunBinding


class ListKebunFragment : Fragment() {
    private var _binding: FragmentListKebunBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel:PetaniKebunViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(
            requireActivity(), ViewModelFactory.getInstance(requireActivity().application)
        )[PetaniKebunViewModel::class.java]

        val layoutManager = LinearLayoutManager(requireActivity())
        this.binding.rvListKebun.layoutManager=layoutManager

        val kebunAdapter=AllKebunAdapter()
        this.binding.rvListKebun.adapter=kebunAdapter

        viewModel.allKebun.observe(requireActivity()){
            if(isAdded){
                kebunAdapter.list=it.toMutableList()
                kebunAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =FragmentListKebunBinding.inflate(inflater,container,false)
        return this.binding.root
    }

}