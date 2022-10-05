package com.taburtuaigroup.taburtuai.ui.listpetanikebun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.databinding.FragmentListPetaniBinding


class ListPetaniFragment : Fragment() {
    private var _binding: FragmentListPetaniBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PetaniKebunViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel=ViewModelProvider(
            requireActivity(),ViewModelFactory.getInstance(requireActivity().application)
        )[PetaniKebunViewModel::class.java]

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvListPetani.layoutManager=layoutManager

        val petaniAdapter=AllPetaniAdapter()
        binding.rvListPetani.adapter=petaniAdapter

        viewModel.allPetani.observe(requireActivity()){
            if(isAdded){
                petaniAdapter.list=it.toMutableList()
                petaniAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentListPetaniBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}