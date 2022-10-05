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
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.util.DateConverter
import com.taburtuaigroup.taburtuai.util.TextFormater

class TerpopulerAdapter(private val onClick: ((Any) -> Unit)): RecyclerView.Adapter<TerpopulerAdapter.TerpopulerVH>() {


    private lateinit var ctx:Context
    var list = mutableListOf<Any>()

    fun submitList(mList: MutableList<Any>) {
        list = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TerpopulerAdapter.TerpopulerVH {
        ctx = parent.context
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_terpopuler, parent, false)
        return TerpopulerVH(view)
    }

    override fun onBindViewHolder(holder: TerpopulerAdapter.TerpopulerVH, position: Int) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int=list.size
    inner class TerpopulerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title_terpopuler)
        private val img: ImageView = itemView.findViewById(R.id.img_terpopuler)

        fun bind(data: Any) {
            if(data is Artikel){
                tvTitle.text = TextFormater.toTitleCase(data.title)
                Glide.with(itemView)
                    .load(data.img_header)
                    .placeholder(R.drawable.placeholder_kebun_square)
                    .into(img)

                itemView.setOnClickListener {
                    onClick(data)
                }
            }else if(data is PenyakitTumbuhan){
                tvTitle.text = TextFormater.toTitleCase(data.title)
                Glide.with(itemView)
                    .load(data.img_header)
                    .placeholder(R.drawable.placeholder_kebun_square)
                    .into(img)

                itemView.setOnClickListener {
                    onClick(data)
                }
            }
        }
    }
}