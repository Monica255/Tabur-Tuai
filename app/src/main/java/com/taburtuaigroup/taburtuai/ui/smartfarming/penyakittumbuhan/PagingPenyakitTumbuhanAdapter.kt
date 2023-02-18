package com.taburtuaigroup.taburtuai.ui.smartfarming.penyakittumbuhan

import android.content.Context
import android.util.Log
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
import com.taburtuaigroup.taburtuai.core.domain.model.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.core.util.DateConverter
import com.taburtuaigroup.taburtuai.core.util.TextFormater

class PagingPenyakitTumbuhanAdapter(private val onClick: ((PenyakitTumbuhan) -> Unit), private val onCheckChanged:((PenyakitTumbuhan)->Unit)?) :
    PagingDataAdapter<PenyakitTumbuhan, PagingPenyakitTumbuhanAdapter.PenyakitTumbuhanVH>(Companion)  {

    lateinit var ctx: Context
    private val uid=FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PenyakitTumbuhanVH {
        ctx=parent.context
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_penyakit_tumbuhan, parent, false)
        return PenyakitTumbuhanVH(view)
    }

    override fun onBindViewHolder(holder: PenyakitTumbuhanVH, position: Int) {
        val data=getItem(position)
        if(data!=null){
            holder.bind(data)
            Log.d("TAG",data.title)
        }
    }

    inner class PenyakitTumbuhanVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title_penyakit)
        private val imgKebun: ImageView = itemView.findViewById(R.id.img_penyakit)
        private val tvDes: TextView = itemView.findViewById(R.id.tv_deskripsi_penyakit)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val cbFav: CheckBox = itemView.findViewById(R.id.cb_fav)

        fun bind(penyakit: PenyakitTumbuhan) {
            if (onCheckChanged != null && penyakit.favorites != null && uid != null) {
                cbFav.visibility = View.VISIBLE
                if (penyakit.favorites != null && uid != null) {
                    cbFav.isChecked = penyakit.favorites!!.contains(uid)
                } else {
                    cbFav.isChecked = false
                }

                cbFav.setOnClickListener {
                    onCheckChanged.invoke(penyakit)
                }


            } else {
                cbFav.isChecked = false
                cbFav.visibility = View.GONE
            }
            tvTitle.text = TextFormater.toTitleCase(penyakit.title)
            tvDes.text=penyakit.deskripsi
            tvDate.text= DateConverter.convertMillisToDate(penyakit.timestamp,ctx)
            Glide.with(itemView)
                .load(penyakit.img_header)
                .placeholder(R.drawable.placeholder_kebun_square)
                .into(imgKebun)

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