@file:Suppress("NAME_SHADOWING")

package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Artikel
import com.taburtuaigroup.taburtuai.databinding.FragmentArtikelBinding
import com.taburtuaigroup.taburtuai.core.util.ARTIKEL_ID
import com.taburtuaigroup.taburtuai.core.util.KategoriArtikel
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtikelFragment : Fragment() {

    private var _binding: FragmentArtikelBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtikelViewModel by activityViewModels()
    private lateinit var adapterTerpopuler: TerpopulerAdapter
    private lateinit var artikelAdapter: ArtikelAdapter
    private var terpopuler = listOf<Artikel>()
    private var artikel = listOf<Artikel>()

    private val onCLickArtikel: ((Artikel) -> Unit) = { artikel ->
        val intent = Intent(requireActivity(), DetailArtikelActivity::class.java)
        intent.putExtra(ARTIKEL_ID, artikel.id_artikel)
        startActivity(intent)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btMuatLebih.setOnClickListener {
            findNavController().navigate(R.id.action_artikelFragment_to_listArtikelFragment)
            viewModel.currentDes = findNavController().currentDestination?.label.toString()
        }
        val layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvArtikelTerpopuler.layoutManager = layoutManager

        val layoutManager2 = LinearLayoutManager(requireActivity())
        binding.rvArtikel.layoutManager = layoutManager2

        adapterTerpopuler = TerpopulerAdapter { data ->
            val intent = Intent(requireActivity(), DetailArtikelActivity::class.java)
            intent.putExtra(ARTIKEL_ID, (data as Artikel).id_artikel)
            startActivity(intent)
        }

        binding.rvArtikelTerpopuler.adapter = adapterTerpopuler

        artikelAdapter = ArtikelAdapter(onCLickArtikel, null)
        binding.rvArtikel.adapter = artikelAdapter

        viewModel.currentDes = findNavController().currentDestination?.label.toString()
        showListTerpopuler(terpopuler)
        showListArtikel(artikel)
        viewModel.getArtikelTerpopuler().observe(requireActivity()) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    it.data?.let { it->
                        if (terpopuler != it) {
                            terpopuler = it
                            showListTerpopuler(terpopuler)
                        }
                        binding.rgKategori.check(
                            when (viewModel.mKategoriArtikel) {
                                KategoriArtikel.SEMUA -> R.id.rb_semua
                                KategoriArtikel.INFORMASI -> R.id.rb_informasi
                                KategoriArtikel.EDUKASI -> R.id.rb_edukasi
                                else -> R.id.rb_lainnya
                            }
                        )
                        observeListArtikel(viewModel.mKategoriArtikel)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    ToastUtil.makeToast(requireContext(), it.message.toString())
                }
            }

        }

        binding.rbSemua.setOnClickListener {
            if (viewModel.mKategoriArtikel != KategoriArtikel.SEMUA)
                observeListArtikel(KategoriArtikel.SEMUA)
        }
        binding.rbInformasi.setOnClickListener {
            if (viewModel.mKategoriArtikel != KategoriArtikel.INFORMASI)
                observeListArtikel(KategoriArtikel.INFORMASI)
        }
        binding.rbEdukasi.setOnClickListener {
            if (viewModel.mKategoriArtikel != KategoriArtikel.EDUKASI)
                observeListArtikel(KategoriArtikel.EDUKASI)
        }
        binding.rbLainnya.setOnClickListener {
            if (viewModel.mKategoriArtikel != KategoriArtikel.LAINNYA)
                observeListArtikel(KategoriArtikel.LAINNYA)
        }


    }

    private fun observeListArtikel(kategoriArtikel: KategoriArtikel) {
        viewModel.getListArtikelByKategori(kategoriArtikel).observe(requireActivity()) {
            when (it) {
                is Resource.Loading -> showLoading(true)
                is Resource.Success -> {
                    showLoading(false)
                    var newList = it.data?.toMutableList()?.minus(terpopuler.toSet())
                    if(newList!=null) {
                        newList = newList.take(if (newList.size >= 4) 4 else newList.size)
                        if (artikel != newList) {
                            artikel = newList
                            showListArtikel(newList)
                        }
                    }else showListArtikel(listOf())

                }
                is Resource.Error -> {
                    showLoading(false)
                    ToastUtil.makeToast(requireContext(), it.message.toString())
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pbLoading.visibility = View.VISIBLE
            //binding.rvArtikel.visibility=View.GONE
            //binding.btMuatLebih.visibility=View.GONE
        } else {
            binding.pbLoading.visibility = View.GONE
            //binding.rvArtikel.visibility=View.VISIBLE
            //binding.btMuatLebih.visibility=View.VISIBLE
        }
    }

    private fun showListTerpopuler(list: List<Artikel>) {
        if (list.isNotEmpty()) {
            binding.tvArtikelTerpopulerLabel.visibility = View.VISIBLE
            binding.rvArtikelTerpopuler.visibility = View.VISIBLE
            adapterTerpopuler.submitList(list.toMutableList())
        } else {
            binding.tvArtikelTerpopulerLabel.visibility = View.GONE
            binding.rvArtikelTerpopuler.visibility = View.GONE
        }
    }

    private fun showListArtikel(list: List<Artikel>) {
        if (list.isNotEmpty()) {
            binding.rvArtikel.visibility = View.VISIBLE
            artikelAdapter.submitList(list.toMutableList())
        } else {
            binding.rvArtikel.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtikelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}