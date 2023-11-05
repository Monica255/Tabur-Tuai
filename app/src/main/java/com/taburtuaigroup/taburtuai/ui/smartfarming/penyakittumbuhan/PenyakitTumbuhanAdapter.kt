package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.core.util.DateConverter
import com.taburtuaigroup.taburtuai.core.util.TextFormater
import com.taburtuaigroup.taburtuai.databinding.ItemPenyakitTumbuhanBinding

class PenyakitTumbuhanAdapter(
    private val onClick: ((PenyakitTumbuhan) -> Unit),
    private val onCheckChanged: ((PenyakitTumbuhan) -> Unit)?
) :
    RecyclerView.Adapter<PenyakitTumbuhanAdapter.PenyakitTumbuhanVH>() {

    var list = mutableListOf<PenyakitTumbuhan>()
    private val uid=FirebaseAuth.getInstance().currentUser?.uid

    fun submitList(mList: MutableList<PenyakitTumbuhan>) {
        list = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PenyakitTumbuhanVH {
        val binding=ItemPenyakitTumbuhanBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PenyakitTumbuhanVH(binding)
    }

    override fun onBindViewHolder(
        holder: PenyakitTumbuhanVH,
        position: Int
    ) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = list.size

    inner class PenyakitTumbuhanVH(private val binding: ItemPenyakitTumbuhanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(penyakit: PenyakitTumbuhan) {
            binding.cbFav.visibility=View.GONE
            binding.tvTitlePenyakit.text = TextFormater.toTitleCase(penyakit.title)
            binding.tvDeskripsiPenyakit.text = penyakit.deskripsi
            binding.tvDate.text = DateConverter.convertMillisToDate(penyakit.timestamp, binding.root.context)



            if(onCheckChanged!=null){
                binding.cbFav.visibility=View.VISIBLE
                if(penyakit.favorites!=null&&uid!=null){
                    binding.cbFav.isChecked = penyakit.favorites!!.contains(uid)
                }else{
                    binding.cbFav.isChecked=false
                }
                binding.cbFav.setOnClickListener {
                    onCheckChanged.invoke(penyakit)
                    if(penyakit.favorites!=null&&uid!=null){
                        binding.cbFav.isChecked = !penyakit.favorites!!.contains(uid)
                        if(!penyakit.favorites!!.contains(uid)){
                            penyakit.favorites?.add(uid)
                        }else{
                            penyakit.favorites?.remove(uid)
                        }
                    }else{
                        binding.cbFav.isChecked=false
                    }
                }

            }else{
                binding.cbFav.visibility=View.GONE
            }


            Glide.with(itemView)
                .load(penyakit.img_header)
                .placeholder(R.drawable.placeholder_kebun_square)
                .into(binding.imgPenyakit)

            itemView.setOnClickListener {
                onClick(penyakit)
            }


        }
    }


}