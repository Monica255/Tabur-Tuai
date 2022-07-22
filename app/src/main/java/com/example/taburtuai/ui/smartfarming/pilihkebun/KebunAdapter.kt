package com.example.taburtuai.ui.smartfarming.pilihkebun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taburtuai.R
import com.example.taburtuai.data.Kebun

class KebunAdapter(private val onClick: ((Kebun) -> Unit)) :
    RecyclerView.Adapter<KebunAdapter.KebunViewHolder>() {

    var list= listOf<Kebun>()

    fun submitList(mList:List<Kebun>){
        list=mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KebunViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_kebun, parent, false)
        return KebunViewHolder(view)
    }

    /*override fun onBindViewHolder(holder: KebunViewHolder, position: Int) {
        val habit=getItem(position) as Kebun
        holder.bind(habit)
    }*/
    inner class KebunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tv_kebun_name)
        private val imgKebun: ImageView = itemView.findViewById(R.id.img_kebun)

        lateinit var getHabit: Kebun
        fun bind(kebun: Kebun) {
            getHabit = kebun
            tvTitle.text = if(kebun.nama_kebun!="") kebun.nama_kebun else kebun.id_kebun

            Glide.with(itemView)
                .load(kebun.img_kebun)
                .placeholder(R.drawable.placeholder_kebun_square)
                .into(imgKebun)

            itemView.setOnClickListener {
                onClick(kebun)
            }
        }

    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Kebun>() {
            override fun areItemsTheSame(oldItem: Kebun, newItem: Kebun): Boolean {
                return oldItem.id_kebun == newItem.id_kebun
            }

            override fun areContentsTheSame(oldItem: Kebun, newItem: Kebun): Boolean {
                return oldItem == newItem
            }
        }

    }

    override fun onBindViewHolder(holder: KebunViewHolder, position: Int) {
        val kebun=list[position]
        holder.bind(kebun)
    }

    override fun getItemCount()=list.size
}