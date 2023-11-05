package com.taburtuaigroup.taburtuai.ui.smartfarming.artikel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.core.domain.model.Artikel
import com.taburtuaigroup.taburtuai.core.domain.model.Topic
import com.taburtuaigroup.taburtuai.databinding.ItemFilterTopicBinding

class FilterTopicAdapter(
    private val onClick: ((Topic) -> Unit),
) :
    RecyclerView.Adapter<FilterTopicAdapter.FillterTopicVH>() {

    var list = mutableListOf<Topic>()
    //lateinit var ctx: Context
    private val uid=FirebaseAuth.getInstance().currentUser?.uid

    fun submitList(mList: MutableList<Topic>) {
        list = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FillterTopicVH {
        //ctx = parent.context
        val binding =
            ItemFilterTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FillterTopicVH(binding)
    }

    override fun onBindViewHolder(holder: FillterTopicVH, position: Int) {
        val data = list[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = list.size

    inner class FillterTopicVH(private val binding: ItemFilterTopicBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: Topic) {
            binding.root.setOnClickListener {
                onClick(topic)
            }
            binding.root.text=topic.topic_name
        }
    }
}