package com.taburtuaigroup.taburtuai.ui.listpetanikebun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.Petani

class AllPetaniAdapter : RecyclerView.Adapter<AllPetaniAdapter.ItemViewHolder>() {

    var list= mutableListOf<Petani>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_petani_vertical, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllPetaniAdapter.ItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount()=list.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_petani_name)
        private val tvId: TextView = itemView.findViewById(R.id.tv_petani_id)

        fun bind(petani: Petani) {
            tvName.text=if(petani.nama_petani!="")petani.nama_petani else "-"
            tvId.text=petani.id_petani
        }
    }

}