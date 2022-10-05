package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.databinding.FragmentPenyakitTumbuhanBinding
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.ArtikelAdapter
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.DetailArtikelActivity
import com.taburtuaigroup.taburtuai.ui.smartfarming.artikel.TerpopulerAdapter
import com.taburtuaigroup.taburtuai.util.ARTIKEL_ID
import com.taburtuaigroup.taburtuai.util.PENYAKIT_ID

class PenyakitTumbuhanFragment : Fragment() {
    private var _binding: FragmentPenyakitTumbuhanBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PenyakitTumbuhanViewModel
    private lateinit var adapterTerpopuler: TerpopulerAdapter
    private lateinit var penyakitlAdapter: PenyakitTumbuhanAdapter
    private var terpopuler= listOf<PenyakitTumbuhan>()
    private var penyakit= listOf<PenyakitTumbuhan>()


    private val onCLickPenyakit: ((PenyakitTumbuhan) -> Unit) = { artikel ->
        val intent = Intent(requireActivity(), DetailPenyakitActivity::class.java)
        intent.putExtra(PENYAKIT_ID, artikel.id_penyakit)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btMuatLebih.setOnClickListener {
            findNavController().navigate(R.id.action_penyakitTumbuhanFragment2_to_listPenyakitTumbuhanFragment)
            viewModel.currentDes = findNavController().currentDestination?.label.toString()
        }
        val layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPenyakitTerpopuler.layoutManager = layoutManager

        val layoutManager2 = LinearLayoutManager(requireActivity())
        binding.rvPenyakitTumbuhan.layoutManager = layoutManager2

        adapterTerpopuler = TerpopulerAdapter { data ->
            val intent = Intent(requireActivity(), DetailPenyakitActivity::class.java)
            intent.putExtra(PENYAKIT_ID, (data as PenyakitTumbuhan).id_penyakit)
            startActivity(intent)
        }

        binding.rvPenyakitTerpopuler.adapter = adapterTerpopuler

        penyakitlAdapter = PenyakitTumbuhanAdapter(onCLickPenyakit, null)
        binding.rvPenyakitTumbuhan.adapter = penyakitlAdapter

        viewModel =
            ViewModelProvider(
                requireActivity(),
                ViewModelFactory.getInstance(requireActivity().application)
            )[PenyakitTumbuhanViewModel::class.java]

        viewModel.getPenyakitTerpopuler()
        viewModel.currentDes = findNavController().currentDestination?.label.toString()
        showListTerpopuler(terpopuler)
        showListArtikel(penyakit)
        viewModel.getListPenyakit()
        viewModel.listPenyakitTerpopuler.observe(requireActivity()) { terpopuler ->
            if(terpopuler!=this.terpopuler){
                this.terpopuler=terpopuler
                showListTerpopuler(terpopuler)
            }
            viewModel.listPenyakit.observe(requireActivity()) {
                var newList = it.toMutableList().minus(terpopuler.toSet())
                newList = newList.take(if (newList.size >= 4) 4 else newList.size)
                if(penyakit!=newList){
                    penyakit=newList
                    showListArtikel(newList)
                }
            }
        }

        viewModel.isLoading.observe(requireActivity()){
            showLoading(it)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPenyakitTumbuhanBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun showLoading(isLoading:Boolean){
        if(isLoading){
            binding.pbLoading.visibility=View.VISIBLE
            //binding.rvArtikel.visibility=View.GONE
            //binding.btMuatLebih.visibility=View.GONE
        }else{
            binding.pbLoading.visibility=View.GONE
            //binding.rvArtikel.visibility=View.VISIBLE
            //binding.btMuatLebih.visibility=View.VISIBLE
        }
    }

    private fun showListTerpopuler(list: List<PenyakitTumbuhan>) {
        if (list.isNotEmpty()) {
            binding.tvPenyakitTerpopulerLabel.visibility = View.VISIBLE
            binding.rvPenyakitTerpopuler.visibility = View.VISIBLE
            adapterTerpopuler.submitList(list.toMutableList())
        } else {
            binding.tvPenyakitTerpopulerLabel.visibility = View.GONE
            binding.rvPenyakitTerpopuler.visibility = View.GONE
        }
    }

    private fun showListArtikel(list: List<PenyakitTumbuhan>) {
        if (list.isNotEmpty()) {
            binding.rvPenyakitTumbuhan.visibility = View.VISIBLE
            penyakitlAdapter.submitList(list.toMutableList())
        } else {
            binding.rvPenyakitTumbuhan.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}