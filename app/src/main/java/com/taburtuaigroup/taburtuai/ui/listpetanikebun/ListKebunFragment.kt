package com.taburtuaigroup.taburtuai.ui.listpetanikebun

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.databinding.FragmentListKebunBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListKebunFragment : Fragment() {
    private var _binding: FragmentListKebunBinding? = null
    private val binding get() = _binding!!
    private val viewModel:PetaniKebunViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        this.binding.rvListKebun.layoutManager=layoutManager

        val kebunAdapter=AllKebunAdapter()
        this.binding.rvListKebun.adapter=kebunAdapter

        viewModel.allKebun.observe(requireActivity()){
            if(isAdded){
                when(it){
                    is Resource.Loading->{
                        Log.d("TAG","loading petani")
                    }
                    is Resource.Success->{
                        Log.d("TAG",it.data.toString())
                        kebunAdapter.list= it.data?.toMutableList() ?: mutableListOf()
                        kebunAdapter.notifyDataSetChanged( )
                    }
                    is Resource.Error->{
                        Log.d("TAG",it.message.toString())
                    }
                }
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