package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import android.content.ClipData.Item
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
import com.taburtuaigroup.taburtuai.core.domain.model.Artikel
import com.taburtuaigroup.taburtuai.core.util.DateConverter
import com.taburtuaigroup.taburtuai.core.util.TextFormater
import com.taburtuaigroup.taburtuai.databinding.ItemArtikelBinding

class ArtikelAdapter(
    private val onClick: ((Artikel) -> Unit),
    private val onCheckChange: ((Artikel) -> Unit)?
) :
    RecyclerView.Adapter<ArtikelAdapter.ArtikelVH>() {

    var list = mutableListOf<Artikel>()
    //lateinit var ctx: Context
    private val uid=FirebaseAuth.getInstance().currentUser?.uid

    fun submitList(mList: MutableList<Artikel>) {
        list = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArtikelVH {
        //ctx = parent.context
        val binding =
            ItemArtikelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtikelVH(binding)
    }

    override fun onBindViewHolder(holder: ArtikelVH, position: Int) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = list.size

    inner class ArtikelVH(private val binding: ItemArtikelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(artikel: Artikel) {

            binding.tvTitleArtikel.text = TextFormater.toTitleCase(artikel.title)
            binding.tvArtikel.text = artikel.artikel_text
            binding.tvDate.text = DateConverter.convertMillisToDate(artikel.timestamp, binding.root.context)

            if(onCheckChange!=null){
                binding.cbFav.visibility=View.VISIBLE
                if(artikel.favorites!=null&&uid!=null){
                    binding.cbFav.isChecked = artikel.favorites!!.contains(uid)
                }else{
                    binding.cbFav.isChecked=false
                }

                binding.cbFav.setOnClickListener {
                    onCheckChange.invoke(artikel)
                    if(artikel.favorites!=null&&uid!=null){
                        binding.cbFav.isChecked = !artikel.favorites!!.contains(uid)
                        if(!artikel.favorites!!.contains(uid)){
                            artikel.favorites?.add(uid)
                        }else{
                            artikel.favorites?.remove(uid)
                        }
                    }else{
                        binding.cbFav.isChecked=false
                    }
                }
            }else{
                binding.cbFav.visibility=View.GONE
            }
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
}