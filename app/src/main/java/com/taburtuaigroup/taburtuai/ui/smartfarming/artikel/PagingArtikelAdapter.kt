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

class PagingArtikelAdapter(
    private val onClick: ((Artikel) -> Unit),
    private val onCheckChanged: ((Artikel) -> Unit)?
) : PagingDataAdapter<Artikel, PagingArtikelAdapter.ArtikelVH>(
    Companion
) {
    lateinit var ctx: Context
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    inner class ArtikelVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title_artikel)
        private val imgKebun: ImageView = itemView.findViewById(R.id.img_header_artikel)
        private val tvDes: TextView = itemView.findViewById(R.id.tv_artikel)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val cbFav: CheckBox = itemView.findViewById(R.id.cb_fav)
        private val tvKategori: TextView = itemView.findViewById(R.id.tv_kategori_artikel)

        fun bind(artikel: Artikel) {
            if (onCheckChanged != null && artikel.favorites != null && uid != null) {
                cbFav.visibility = View.VISIBLE
                if (artikel.favorites != null && uid != null) {
                    cbFav.isChecked = artikel.favorites!!.contains(uid)
                } else {
                    cbFav.isChecked = false
                }
                cbFav.setOnClickListener {
                    onCheckChanged.invoke(artikel)
                }

            } else {
                cbFav.isChecked = false
                cbFav.visibility = View.GONE
            }

            tvTitle.text = TextFormater.toTitleCase(artikel.title)
            tvDes.text = artikel.artikel_text
            tvDate.text = DateConverter.convertMillisToDate(artikel.timestamp, ctx)
            tvKategori.text = TextFormater.toTitleCase(artikel.kategori)

            Glide.with(itemView)
                .load(artikel.img_header)
                .placeholder(R.drawable.placeholder_kebun_square)
                .into(imgKebun)

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
        ctx = parent.context
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_artikel, parent, false)
        return ArtikelVH(view)
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