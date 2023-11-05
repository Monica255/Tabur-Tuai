package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.Artikel
import com.taburtuaigroup.taburtuai.core.util.DateConverter
import com.taburtuaigroup.taburtuai.core.util.TextFormater
import com.taburtuaigroup.taburtuai.databinding.ItemArtikelBinding

class PagingArtikelAdapter(
    private val onClick: ((Artikel) -> Unit),
    private val onCheckChanged: ((Artikel) -> Unit)?
) : PagingDataAdapter<Artikel, PagingArtikelAdapter.ArtikelVH>(
    Companion
) {
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    inner class ArtikelVH(private val binding: ItemArtikelBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(artikel: Artikel) {
            if (onCheckChanged != null && artikel.favorites != null && uid != null) {
                binding.cbFav.visibility = View.VISIBLE
                if (artikel.favorites != null && uid != null) {
                    binding.cbFav.isChecked = artikel.favorites!!.contains(uid)
                } else {
                    binding.cbFav.isChecked = false
                }
                binding.cbFav.setOnClickListener {
                    onCheckChanged.invoke(artikel)
                }

            } else {
                binding.cbFav.isChecked = false
                binding.cbFav.visibility = View.GONE
            }

            binding.tvTitleArtikel.text = TextFormater.toTitleCase(artikel.title)
            binding.tvArtikel.text = artikel.artikel_text
            binding.tvDate.text = DateConverter.convertMillisToDate(artikel.timestamp, binding.root.context)
            binding.tvKategoriArtikel.text = TextFormater.toTitleCase(artikel.kategori)

            Glide.with(itemView)
                .load(artikel.img_header)
                .placeholder(R.drawable.placeholder_kebun_square)
                .into(binding.imgHeaderArtikel)

            itemView.setOnClickListener {
                onClick(artikel)
            }
        }

    }

    override fun onBindViewHolder(holder: ArtikelVH, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtikelVH {
        val binding =
            ItemArtikelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtikelVH(binding)
    }

    companion object : DiffUtil.ItemCallback<Artikel>() {
        override fun areItemsTheSame(oldItem: Artikel, newItem: Artikel): Boolean {
            return oldItem.id_artikel == newItem.id_artikel
        }

        override fun areContentsTheSame(oldItem: Artikel, newItem: Artikel): Boolean {
            return oldItem == newItem
        }
    }

}