package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.kebun

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.data.DailyWeather
import com.taburtuaigroup.taburtuai.data.Device
import com.taburtuaigroup.taburtuai.data.WeatherEntity

class WeatherForcastAdapter() :
    RecyclerView.Adapter<WeatherForcastAdapter.ItemViewHolder>() {

    var list = listOf<DailyWeather>()
    fun submitList(mList: List<DailyWeather>) {
        if(mList!=list){
            list=mList
            notifyDataSetChanged()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_perkiraan_cuaca, parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val item:CustomPerkiraanCuaca=itemView.findViewById(R.id.custom_perkiraan_cuaca)

        fun bind(data: DailyWeather) {
            tvDate.text="${data.date.date}/${data.date.month}"
            item.setData(data)
        }
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
            holder.bind(item)

    }

    override fun getItemCount() = list.size
}