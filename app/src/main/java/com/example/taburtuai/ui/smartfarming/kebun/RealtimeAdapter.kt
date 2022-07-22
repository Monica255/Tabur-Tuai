package com.example.taburtuai.ui.smartfarming.kebun

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.taburtuai.R
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.data.Device

class RealtimeAdapter(private val onClick: ((Device) -> Unit)?) :
    RecyclerView.Adapter<RealtimeAdapter.ItemViewHolder>() {

    var list = mutableListOf<Device>()
    var isConnected=false
    fun submitList(mList: List<Device>) {
        list = mList.toMutableList()
        notifyDataSetChanged()
        Log.d("TAG", "sd " + list.toString())

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_controlling, parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: ControlDeviceView = itemView.findViewById(R.id.item_controlling_device)

        fun bind(device: Device) {
            tvTitle.setData(device)
            if(device.state==0||device.state==1){
                itemView.setOnClickListener {
                    if(isConnected){
                        onClick?.let { it1 -> it1(device) }
                    }

                }
            }
        }
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
        if (item.id_device != "") {
            holder.bind(item)
        } else {
            list.removeAt(position)
            Log.d("TAG",list.toString())
        }
    }

    override fun getItemCount() = list.size
}