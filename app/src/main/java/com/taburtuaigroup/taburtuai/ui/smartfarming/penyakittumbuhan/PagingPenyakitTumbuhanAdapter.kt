package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.core.util.DateConverter
import com.taburtuaigroup.taburtuai.core.util.TextFormater
import com.taburtuaigroup.taburtuai.databinding.ItemPenyakitTumbuhanBinding

class PagingPenyakitTumbuhanAdapter(private val onClick: ((PenyakitTumbuhan) -> Unit), private val onCheckChanged:((PenyakitTumbuhan)->Unit)?) :
    PagingDataAdapter<PenyakitTumbuhan, PagingPenyakitTumbuhanAdapter.PenyakitTumbuhanVH>(Companion)  {

    private val uid=FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PenyakitTumbuhanVH {
        val binding=ItemPenyakitTumbuhanBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PenyakitTumbuhanVH(binding)
    }

    override fun onBindViewHolder(holder: PenyakitTumbuhanVH, position: Int) {
        val data=getItem(position)
        if(data!=null){
            holder.bind(data)
        }
    }

    inner class PenyakitTumbuhanVH(private val binding: ItemPenyakitTumbuhanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(penyakit: PenyakitTumbuhan) {
            if (onCheckChanged != null && penyakit.favorites != null && uid != null) {
                binding.cbFav.visibility = View.VISIBLE
                if (penyakit.favorites != null && uid != null) {
                    binding.cbFav.isChecked = penyakit.favorites!!.contains(uid)
                } else {
                    binding.cbFav.isChecked = false
                }

                binding.cbFav.setOnClickListener {
                    onCheckChanged.invoke(penyakit)
                }


            } else {
                binding.cbFav.isChecked = false
                binding.cbFav.visibility = View.GONE
            }
            binding.tvTitlePenyakit.text = TextFormater.toTitleCase(penyakit.title)
            binding.tvDeskripsiPenyakit.text=penyakit.deskripsi
            binding.tvDate.text= DateConverter.convertMillisToDate(penyakit.timestamp,binding.root.context)
            Glide.with(itemView)
                .load(penyakit.img_header)
                .placeholder(R.drawable.placeholder_kebun_square)
                .into(binding.imgPenyakit)

            itemView.setOnClickListener {
                onClick(penyakit)
            }


        }

    }

    companion object : DiffUtil.ItemCallback<PenyakitTumbuhan>() {
        override fun areItemsTheSame(oldItem: PenyakitTumbuhan, newItem: PenyakitTumbuhan): Boolean {
            return oldItem.id_penyakit == newItem.id_penyakit
        }

        override fun areContentsTheSame(oldItem: PenyakitTumbuhan, newItem: PenyakitTumbuhan): Boolean {
            return oldItem == newItem
        }
    }
}