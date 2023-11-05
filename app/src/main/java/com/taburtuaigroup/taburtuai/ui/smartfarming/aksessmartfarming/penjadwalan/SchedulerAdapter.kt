package com.taburtuaigroup.taburtuai.ui.smartfarming.aksessmartfarming.penjadwalan

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.Mscheduler
import com.taburtuaigroup.taburtuai.core.util.TextFormater
import com.taburtuaigroup.taburtuai.databinding.ItemSchedulerBinding

class SchedulerAdapter(
    private val onDelete: ((Mscheduler) -> Unit),
    private val onSwitchChange: (Mscheduler, Boolean) -> Unit
) :
    RecyclerView.Adapter<SchedulerAdapter.ItemViewHolder>() {

    var list = mutableListOf<Mscheduler>()
    var isConnected = false

    fun deleteScheduler(index: Int) {
        list.removeAt(index)
        notifyItemRemoved(index)
    }

    fun submitList(mList: MutableList<Mscheduler>) {
        list = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemSchedulerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    inner class ItemViewHolder(private val binding: ItemSchedulerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: Mscheduler) {
            val device = schedule.id_device.split("_")[0]
            binding.imgDevice.setImageResource(
                when (device) {
                    "pompa" -> if (schedule.action == 1) R.drawable.icon_pompa else R.drawable.icon_pompa_off
                    "sprinkler" -> if (schedule.action == 1) R.drawable.icon_sprinkle else R.drawable.icon_sprinkle_off
                    "lampu" -> if (schedule.action == 1) R.drawable.icon_lightbulb else R.drawable.icon_lightbulb_off
                    "cctv" -> if (schedule.action == 1) R.drawable.icon_cctv_1 else R.drawable.icon_cctv_off
                    "drip" -> if (schedule.action == 1) R.drawable.icon_drip else R.drawable.icon_drip_off
                    "fogger" -> if (schedule.action == 1) R.drawable.icon_fogger else R.drawable.icon_fogger_off
                    else -> R.drawable.icon_placeholder_2
                }
            )
            binding.tvTime.text = TextFormater.toClockFormat(schedule.hour, schedule.minute)
            binding.tvFarm.text = schedule.id_kebun
            binding.tvDevice.text = schedule.id_device
            binding.swScheduler.isChecked = schedule.active
            Log.d("TAG", schedule.id_scheduler.toString() + " " + schedule.active.toString())

            binding.swScheduler.setOnClickListener {
                val active=binding.swScheduler.isChecked
                Log.d(
                    "TAG",
                    schedule.id_scheduler.toString() + " " + schedule.active.toString() + " " + active
                )
                if (schedule.active != active) {
                    Log.d("TAG", "Different")
                    onSwitchChange(schedule, active)
                    var newSchedule = Mscheduler(
                        schedule.id_scheduler,
                        schedule.action,
                        schedule.id_kebun,
                        schedule.id_device,
                        schedule.id_petani,
                        schedule.hour,
                        schedule.minute,
                        active
                    )
                    val index=list.indexOf(schedule)
                    Log.d("TAG","idx "+index)
                    list.removeAt(index)
                    list.add(index, newSchedule)
                    notifyItemChanged(index)
                }
            }


            binding.imgDelete.setOnClickListener { onDelete(schedule) }
        }
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount() = list.size
}