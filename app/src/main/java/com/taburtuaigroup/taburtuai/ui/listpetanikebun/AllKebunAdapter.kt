package com.taburtuaigroup.taburtuai.ui.listpetanikebun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.Kebun

class AllKebunAdapter : RecyclerView.Adapter<AllKebunAdapter.ItemViewHolder>() {

    var list= mutableListOf<Kebun>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_kebun_vertical, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllKebunAdapter.ItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount()=list.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_kebun_name_iv)
        private val tvId: TextView = itemView.findViewById(R.id.tv_kebun_id_iv)
        private val tvIdPetani: TextView = itemView.findViewById(R.id.tv_petani_id_iv)

        fun bind(kebun: Kebun) {
            tvName.text=if(kebun.nama_kebun!="")kebun.nama_kebun else "-"
            tvId.text=kebun.id_kebun
            tvIdPetani.text=kebun.id_petani
        }
    }

}