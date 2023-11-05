package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.penjadwalan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Kebun
import com.taburtuaigroup.taburtuai.core.domain.model.Petani
import com.taburtuaigroup.taburtuai.core.domain.model.Topic
import com.taburtuaigroup.taburtuai.core.util.EXTRA_PETANI
import com.taburtuaigroup.taburtuai.core.util.KEBUN_ID
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.databinding.FragmentForumTopicBinding
import com.taburtuaigroup.taburtuai.databinding.FragmentPilihKebunBinding
import com.taburtuaigroup.taburtuai.ui.komunitas.forum.OnGetDataTopic
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun.KebunActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.pilihkebun.KebunAdapter
import com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.pilihkebun.PilihKebunViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PilihKebunFragment : DialogFragment() {
    private var _binding: FragmentPilihKebunBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateScheduleViewModel by activityViewModels()
    private var idPetani: String? = null
    private lateinit var kebunAdapter: KebunAdapter
    private lateinit var onGetData: OnGetData

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onGetData = activity as OnGetData
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

        kebunAdapter = KebunAdapter { kebun ->
            onGetData.handleData(kebun)
            dismiss()
        }
        binding.rvKebun.adapter = kebunAdapter


        val x = viewModel.petani
        if (x != null) {
            binding.toolbarTitle.text = getString(R.string.kebun_petani, x.nama_petani)
            idPetani = x.id_petani
            viewModel.getAllKebun(idPetani = x.id_petani).observe(this) {
                handleDataKebun(it)
            }

        } else {
            dismiss()
        }

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun handleDataKebun(data: Resource<List<Kebun>>) {
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
    private fun showRecyclerView(kebun: List<Kebun>) {
        kebunAdapter.submitList(kebun)
        kebunAdapter.notifyDataSetChanged()
        binding.rvKebun.visibility = if (kebun.isNotEmpty()) View.VISIBLE else View.GONE
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
        _binding= FragmentPilihKebunBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
interface OnGetData {
    fun handleData(data: Kebun?)
}