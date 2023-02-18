package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.databinding.FragmentListPenyakitTumbuhanBinding
import com.taburtuaigroup.taburtuai.core.util.PENYAKIT_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ListPenyakitTumbuhanFragment : Fragment() {
    private var _binding: FragmentListPenyakitTumbuhanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PenyakitTumbuhanViewModel by activityViewModels()
    private lateinit var adapterPenyakit: PagingPenyakitTumbuhanAdapter

    private val onCLickPenyakit: ((PenyakitTumbuhan) -> Unit) = { artikel ->
        val intent = Intent(requireActivity(), DetailPenyakitActivity::class.java)
        intent.putExtra(PENYAKIT_ID, artikel.id_penyakit)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManagerArtikel = LinearLayoutManager(requireActivity())
        binding.rvArtikelSemua.layoutManager = layoutManagerArtikel


        adapterPenyakit = PagingPenyakitTumbuhanAdapter(onCLickPenyakit, null)
        binding.rvArtikelSemua.adapter=adapterPenyakit


        lifecycleScope.launch {
            adapterPenyakit.loadStateFlow.collectLatest { loadStates ->
                showLoading(loadStates.refresh is LoadState.Loading)
            }
        }

        viewModel.getData()
        viewModel.pagingData.observe(requireActivity()){ it ->
            if(isAdded){
                it.observe(requireActivity()){
                    if(it!=null&& isAdded){
                        if(viewModel.mKeyword!=""){
                            binding.tvPenyakitLainnya.text=getString(R.string.hasil_pencarian,viewModel.mKeyword)
                        }else{
                            binding.tvPenyakitLainnya.text=getString(R.string.penyakit_tumbuhan_lainnya)
                        }
                        adapterPenyakit.submitData(lifecycle, it)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListPenyakitTumbuhanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.currentDes=findNavController().currentDestination?.label.toString()
    }


    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility = if (isShowLoading) View.VISIBLE else View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}