package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.databinding.FragmentListArtikelBinding
import com.taburtuaigroup.taburtuai.util.ARTIKEL_ID
import com.taburtuaigroup.taburtuai.util.KategoriArtikel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ListArtikelFragment : Fragment() {
    private var _binding: FragmentListArtikelBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ArtikelViewModel
    private lateinit var adapterArtikel: PagingArtikelAdapter

    private val onCLickArtikel: ((Artikel) -> Unit) = { artikel ->
        val intent = Intent(requireActivity(), DetailArtikelActivity::class.java)
        intent.putExtra(ARTIKEL_ID, artikel.id_artikel)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(
                requireActivity(),
                ViewModelFactory.getInstance(requireActivity().application)
            )[ArtikelViewModel::class.java]

        val layoutManagerArtikel = LinearLayoutManager(requireActivity())
        binding.rvArtikelSemua.layoutManager = layoutManagerArtikel


        adapterArtikel = PagingArtikelAdapter(onCLickArtikel, null)
        binding.rvArtikelSemua.adapter=adapterArtikel

        binding.rbSemua.setOnClickListener {
            if(viewModel.mKategoriArtikel!=KategoriArtikel.SEMUA)viewModel.getData(KategoriArtikel.SEMUA)
        }
        binding.rbInformasi.setOnClickListener {
            if(viewModel.mKategoriArtikel!=KategoriArtikel.INFORMASI)viewModel.getData(KategoriArtikel.INFORMASI)
        }
        binding.rbEdukasi.setOnClickListener {
            if(viewModel.mKategoriArtikel!=KategoriArtikel.EDUKASI)viewModel.getData(KategoriArtikel.EDUKASI)

        }
        binding.rbLainnya.setOnClickListener {
            if(viewModel.mKategoriArtikel!=KategoriArtikel.LAINNYA)viewModel.getData(KategoriArtikel.LAINNYA)
        }
        binding.rgKategori.check(
            when(viewModel.mKategoriArtikel){
                KategoriArtikel.SEMUA->R.id.rb_semua
                KategoriArtikel.INFORMASI->R.id.rb_informasi
                KategoriArtikel.EDUKASI->R.id.rb_edukasi
                else->R.id.rb_lainnya
            }
        )

        lifecycleScope.launch {
            adapterArtikel.loadStateFlow.collectLatest { loadStates ->
                showLoading(loadStates.refresh is LoadState.Loading)
            }
        }

        viewModel.getData(viewModel.mKategoriArtikel)
        viewModel.pagingData.observe(requireActivity()){
            if(isAdded){
                it.observe(requireActivity()){ it ->
                    if(it!=null&& isAdded){
                        if(viewModel.mKeyword!=""){
                            binding.tvArtikelLainnya.text="Hasil pencarian "+viewModel.mKeyword
                        }else{
                            binding.tvArtikelLainnya.text="Artikel lainnya"
                        }
                        adapterArtikel.submitData(lifecycle, it)
                     }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.currentDes=findNavController().currentDestination?.label.toString()
    }


    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility = if (isShowLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListArtikelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}