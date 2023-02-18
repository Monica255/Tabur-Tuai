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

class PenyakitTumbuhanAdapter(
    private val onClick: ((PenyakitTumbuhan) -> Unit),
    private val onCheckChanged: ((PenyakitTumbuhan) -> Unit)?
) :
    RecyclerView.Adapter<PenyakitTumbuhanAdapter.PenyakitTumbuhanVH>() {

    var list = mutableListOf<PenyakitTumbuhan>()
    lateinit var ctx: Context
    private val uid=FirebaseAuth.getInstance().currentUser?.uid

    fun submitList(mList: MutableList<PenyakitTumbuhan>) {
        list = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PenyakitTumbuhanVH {
        ctx = parent.context
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_penyakit_tumbuhan, parent, false)
        return PenyakitTumbuhanVH(view)
    }

    override fun onBindViewHolder(
        holder: PenyakitTumbuhanVH,
        position: Int
    ) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = list.size

    inner class PenyakitTumbuhanVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title_penyakit)
        private val imgKebun: ImageView = itemView.findViewById(R.id.img_penyakit)
        private val tvDes: TextView = itemView.findViewById(R.id.tv_deskripsi_penyakit)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val cbFav: CheckBox = itemView.findViewById(R.id.cb_fav)

        fun bind(penyakit: PenyakitTumbuhan) {
            cbFav.visibility=View.GONE
            tvTitle.text = TextFormater.toTitleCase(penyakit.title)
            tvDes.text = penyakit.deskripsi
            tvDate.text = DateConverter.convertMillisToDate(penyakit.timestamp, ctx)

            if(onCheckChanged!=null){
                cbFav.visibility=View.VISIBLE
                if(penyakit.favorites!=null&&uid!=null){
                    cbFav.isChecked = penyakit.favorites!!.contains(uid)
                }else{
                    cbFav.isChecked=false
                }
                cbFav.setOnClickListener {
                    onCheckChanged.invoke(penyakit)
                    if(penyakit.favorites!=null&&uid!=null){
                        cbFav.isChecked = !penyakit.favorites!!.contains(uid)
                        if(!penyakit.favorites!!.contains(uid)){
                            penyakit.favorites?.add(uid)
                        }else{
                            penyakit.favorites?.remove(uid)
                        }
                    }else{
                        cbFav.isChecked=false
                    }
                }

            }else{
                cbFav.visibility=View.GONE
            }


            Glide.with(itemView)
                .load(penyakit.img_header)
                .placeholder(R.drawable.placeholder_kebun_square)
                .into(imgKebun)

            itemView.setOnClickListener {
                onClick(penyakit)
            }


        }
    }


}