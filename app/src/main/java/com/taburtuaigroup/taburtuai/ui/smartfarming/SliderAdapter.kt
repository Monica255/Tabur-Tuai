package com.taburtuaigroup.taburtuai.ui.smartfarming

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.taburtuaigroup.taburtuai.R
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import com.taburtuaigroup.taburtuai.data.Artikel
import com.taburtuaigroup.taburtuai.data.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.util.DateConverter
import com.taburtuaigroup.taburtuai.util.TextFormater


class SliderAdapter(private val onClick: ((Any) -> Unit)) :
    SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {


    var list = mutableListOf<Any>()
    lateinit var ctx:Context
    fun submitList(list: MutableList<Any>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapter.SliderAdapterVH {
        ctx=parent.context
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_slider, parent, false)
        return SliderAdapterVH(view)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapter.SliderAdapterVH, position: Int) {
        val item = list[position]
        viewHolder.bind(item)
    }

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        //var itemView2: View
        var imageViewBackground: ImageView
        var textTitle: TextView
        var textAuthor: TextView
        var textDate: TextView

        init {
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider)
            textTitle = itemView.findViewById(R.id.tv_auto_image_slider)
            textAuthor = itemView.findViewById(R.id.tv_author)
            textDate = itemView.findViewById(R.id.tv_date)
        }

        fun bind(data: Any) {
            when (data) {
                is Artikel -> {
                    textTitle.text = data.title
                    textAuthor.text = TextFormater.toTitleCase(data.author)
                    textDate.text = DateConverter.convertMillisToDate(data.timestamp,ctx)

                    Glide.with(itemView)
                        .load(data.img_header)
                        .placeholder(R.drawable.placeholder)
                        .into(imageViewBackground)

                    itemView.setOnClickListener {
                        onClick(data)
                    }
                }
                is PenyakitTumbuhan->{
                    textTitle.text = data.title
                    textAuthor.text = TextFormater.toTitleCase(data.author)
                    textDate.text = DateConverter.convertMillisToDate(data.timestamp,ctx)

                    Glide.with(itemView)
                        .load(data.img_header)
                        .placeholder(R.drawable.placeholder)
                        .into(imageViewBackground)

                    itemView.setOnClickListener {
                        onClick(data)
                    }
                }

            }

        }

    }

}