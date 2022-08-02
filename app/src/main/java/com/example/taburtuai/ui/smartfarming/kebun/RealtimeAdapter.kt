package com.example.taburtuai.ui.smartfarming.kebun

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taburtuai.R
import com.example.taburtuai.data.Device

class RealtimeAdapter(private val onClick: ((Device) -> Unit)?) :
    RecyclerView.Adapter<RealtimeAdapter.ItemViewHolder>() {

    var list = mutableListOf<Device>()
    var isConnected=false
    var  clickedItemPos=-1
    fun submitList(mList: MutableList<Device>) {

        if(clickedItemPos!=-1){
            list[clickedItemPos] = mList[clickedItemPos]
            notifyItemChanged(clickedItemPos)
            clickedItemPos=-1
        }else{
            list=mList
            notifyDataSetChanged()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_controlling, parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: ControlDeviceView = itemView.findViewById(R.id.item_controlling_device)

        fun bind(device: Device,position: Int) {
            tvTitle.setData(device)
            if(device.state==0||device.state==1){
                itemView.setOnClickListener {
                    if(isConnected){
                        onClick?.let {it1 -> it1(device);clickedItemPos=position }
                    }
                    /*list[position].name="tess"
                    notifyItemChanged(position)*/

                }
            }
        }
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = list[position]
        if (item.id_device != "") {
            holder.bind(item,position)
        } else {
            list.removeAt(position)
            Log.d("TAG","remove list")
        }
    }

    override fun getItemCount() = list.size
}