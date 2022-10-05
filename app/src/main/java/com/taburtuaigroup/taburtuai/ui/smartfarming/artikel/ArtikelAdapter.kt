package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

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
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.util.DateConverter
import com.taburtuaigroup.taburtuai.util.TextFormater

class ArtikelAdapter(
    private val onClick: ((Artikel) -> Unit),
    private val onCheckChange: ((Artikel) -> Unit)?
) :
    RecyclerView.Adapter<ArtikelAdapter.ArtikelVH>() {

    var list = mutableListOf<Artikel>()
    lateinit var ctx: Context
    private val uid=FirebaseAuth.getInstance().currentUser?.uid
    private var isFav=false

    fun submitList(mList: MutableList<Artikel>) {
        list = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArtikelVH {
        ctx = parent.context
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_artikel, parent, false)
        return ArtikelVH(view)
    }

    override fun onBindViewHolder(holder: ArtikelVH, position: Int) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = list.size

    inner class ArtikelVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title_artikel)
        private val imgKebun: ImageView = itemView.findViewById(R.id.img_header_artikel)
        private val tvDes: TextView = itemView.findViewById(R.id.tv_artikel)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val cbFav: CheckBox = itemView.findViewById(R.id.cb_fav)
        private val tvKategori: TextView = itemView.findViewById(R.id.tv_kategori_artikel)

        fun bind(artikel: Artikel) {

            tvTitle.text = TextFormater.toTitleCase(artikel.title)
            tvDes.text = artikel.artikel_text
            tvDate.text = DateConverter.convertMillisToDate(artikel.timestamp, ctx)

            if(onCheckChange!=null){
                cbFav.visibility=View.VISIBLE
                if(artikel.favorites!=null&&uid!=null){
                    cbFav.isChecked = artikel.favorites!!.contains(uid)
                }else{
                    cbFav.isChecked=false
                }

                cbFav.setOnClickListener {
                    onCheckChange?.invoke(artikel)
                    if(artikel.favorites!=null&&uid!=null){
                        cbFav.isChecked = !artikel.favorites!!.contains(uid)
                        if(!artikel.favorites!!.contains(uid)){
                            artikel.favorites?.add(uid)
                        }else{
                            artikel.favorites?.remove(uid)
                        }
                    }else{
                        cbFav.isChecked=false
                    }
                }
            }else{
                cbFav.visibility=View.GONE
            }


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

}