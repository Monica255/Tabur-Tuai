package com.taburtuaigroup.taburtuai.ui.komunitas.forum

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Topic
import com.taburtuaigroup.taburtuai.core.util.KategoriTopik
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.databinding.FragmentForumTopicBinding
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.FilterTopicAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForumTopicFragment : DialogFragment() {
    private var _binding: FragmentForumTopicBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForumViewModel by viewModels()
    private lateinit var adapterCommonTopic: FilterTopicAdapter
    private lateinit var adapterCommodityTopic: FilterTopicAdapter
    private lateinit var onGetDataTopic: OnGetDataTopic

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onGetDataTopic = activity as OnGetDataTopic
        } catch (e: ClassCastException) {
            Log.e(
                "TAG", "onAttach: ClassCastException: "
                        + e.message
            )
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()
            ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        val layoutManagerCommonTopic = FlexboxLayoutManager(requireActivity())
        layoutManagerCommonTopic.flexDirection = FlexDirection.ROW
        binding.rvCommonTopic.layoutManager = layoutManagerCommonTopic

        val layoutManagerCommodityTopic = FlexboxLayoutManager(requireActivity())
        layoutManagerCommodityTopic.flexDirection = FlexDirection.ROW
        binding.rvCommoditiesTopic.layoutManager = layoutManagerCommodityTopic

        binding.btnAllTopics.setOnClickListener {
            onGetDataTopic.handleDataTopic(null)
            dismiss()
        }

        adapterCommonTopic = FilterTopicAdapter { topic ->
            onGetDataTopic.handleDataTopic(topic)
            dismiss()
        }

        adapterCommodityTopic = FilterTopicAdapter { topic ->
            onGetDataTopic.handleDataTopic(topic)
            dismiss()
        }

        binding.rvCommonTopic.adapter = adapterCommonTopic
        binding.rvCommoditiesTopic.adapter = adapterCommodityTopic

        viewModel.getListTopik(KategoriTopik.COMMON).observe(requireActivity()) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    it.data?.toMutableList()?.let { it1 -> adapterCommonTopic.submitList(it1);Log.d("TAG",it1.toString()) }
                }
                is Resource.Error -> {
                    ToastUtil.makeToast(requireActivity(), "Failed to get topics")
                }
            }
        }

        viewModel.getListTopik(KategoriTopik.COMMODITY).observe(requireActivity()) {
            when (it) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    it.data?.toMutableList()?.let { it1 -> adapterCommodityTopic.submitList(it1) }
                }
                is Resource.Error -> {
                    ToastUtil.makeToast(requireActivity(), "Failed to get topics")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumTopicBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface OnGetDataTopic {
    fun handleDataTopic(data: Topic?)
}