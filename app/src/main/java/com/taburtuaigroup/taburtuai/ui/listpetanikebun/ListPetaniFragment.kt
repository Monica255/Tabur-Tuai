package com.taburtuaigroup.taburtuai.ui.listpetanikebun

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.databinding.FragmentListPetaniBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListPetaniFragment : Fragment() {
    private var _binding: FragmentListPetaniBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PetaniKebunViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvListPetani.layoutManager=layoutManager

        val petaniAdapter=AllPetaniAdapter()
        binding.rvListPetani.adapter=petaniAdapter

        viewModel.allPetani.observe(requireActivity()){
            if(isAdded){
                when(it){
                    is Resource.Loading->{
                        Log.d("TAG","loading petani")
                    }
                    is Resource.Success->{
                        petaniAdapter.list= it.data?.toMutableList() ?: mutableListOf()
                        petaniAdapter.notifyDataSetChanged( )
                    }
                    is Resource.Error->{
                        ToastUtil.makeToast(requireContext(),it.message.toString())
                    }
                }
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