package com.taburtuaigroup.taburtuai.ui.komunitas.forum

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.domain.model.ForumPost
import com.taburtuaigroup.taburtuai.core.util.TextFormater
import com.taburtuaigroup.taburtuai.databinding.ItemPostForumBinding

class PagingForumAdapter(
    private val onClick: ((ForumPost) -> Unit),
    private val onCheckChanged: ((ForumPost) -> Unit),
    private val viewModel: ForumViewModel,
    private val activity: ForumActivity
) : PagingDataAdapter<ForumPost, PagingForumAdapter.ForumVH>(Companion) {
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    inner class ForumVH(private val binding: ItemPostForumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: ForumPost) {
            binding.tvPostTitle.text = post.title
            binding.tvPostContent.text = post.content
            binding.tvKategoriPostForum.text = TextFormater.toTitleCase(post.category)
            binding.tvLikeCount.text = TextFormater.formatLikeCounts(post.like_count)
            binding.tvTimastamp.text = TextFormater.toPostTime(post.timestamp, binding.root.context)
            binding.tvKomentar.text =
                binding.root.context.getString(R.string.komentar, post.comments?.size ?: 0)

            viewModel.getUserdata(post.user_id).observe(activity) {
                it?.let {
                    binding.tvUserName.text = it.name
                    Glide.with(itemView)
                        .load(it.img_profile)
                        .placeholder(R.drawable.placeholder_kebun_square)
                        .into(binding.imgProfilePicture)
                }
            }

            val isLiked=post.likes?.let { it.contains(uid) }?:false
            if (post.likes != null && uid != null) {
                binding.cbLike.isChecked = isLiked
            } else {
                binding.cbLike.isChecked = false
            }

            val doLike: ((Unit) -> Unit) = {
                onCheckChanged.invoke(post)
                if (post.likes != null && uid != null) {
                    binding.cbLike.isChecked = !isLiked
                    if (!isLiked) {
                        //post.likes?.add(uid)
                        val show = ObjectAnimator.ofFloat(binding.imgLike, View.ALPHA, 0.7f).setDuration(600)
                        val disappear = ObjectAnimator.ofFloat(binding.imgLike, View.ALPHA, 0f).setDuration(600)
                        AnimatorSet().apply {
                            playSequentially(show, disappear)
                            start()
                        }
                    } else {
                        //post.likes?.remove(uid)
                    }
                } else {
                    binding.cbLike.isChecked = false
                }
            }

            var doubleClick = false
            binding.root.setOnClickListener {
                if (doubleClick) {
                    if (!isLiked) doLike(Unit)
                    //return@setOnClickListener
                }else {
                    //onClick.invoke(post)
                }


                doubleClick = true
                Handler(Looper.getMainLooper()).postDelayed({ doubleClick = false }, 1000)
            }

            if (post.img_header != null) {
                binding.imgHeaderPost.visibility = View.VISIBLE
                Glide.with(itemView)
                    .load(post.img_header)
                    .placeholder(R.drawable.placeholder_kebun_square)
                    .into(binding.imgHeaderPost)
            } else {
                binding.imgHeaderPost.visibility = View.GONE
            }


            binding.cbLike.setOnClickListener {
                doLike(Unit)
            }
        }
    }

    override fun onBindViewHolder(holder: ForumVH, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForumVH {
        val binding =
            ItemPostForumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForumVH(binding)
    }

    companion object : DiffUtil.ItemCallback<ForumPost>() {
        override fun areItemsTheSame(oldItem: ForumPost, newItem: ForumPost): Boolean {
            return oldItem.id_forum_post == newItem.id_forum_post
        }

        override fun areContentsTheSame(oldItem: ForumPost, newItem: ForumPost): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: ForumPost, newItem: ForumPost): Any? = Any()
    }

}