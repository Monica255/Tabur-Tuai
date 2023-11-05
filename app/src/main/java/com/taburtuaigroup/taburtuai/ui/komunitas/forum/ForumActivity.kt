package com.taburtuaigroup.taburtuai.ui.komunitas.forum

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.ForumPost
import com.taburtuaigroup.taburtuai.core.domain.model.Topic
import com.taburtuaigroup.taburtuai.core.util.*
import com.taburtuaigroup.taburtuai.databinding.ActivityForumBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForumActivity : AppCompatActivity(),OnGetDataTopic {
    private lateinit var binding: ActivityForumBinding
    private lateinit var adapterForum: PagingForumAdapter
    private val viewModel: ForumViewModel by viewModels()
    private var tempPost:ForumPost?=null

    private val onCLick: ((ForumPost) -> Unit) = { post ->
        val intent = Intent(this, DetailForumPostActivity::class.java)
        intent.putExtra(FORUM_POST_ID, post.id_forum_post)
        startActivity(intent)

    }

    private val onCheckChanged: ((ForumPost) -> Unit) = { post ->
        tempPost=post
        viewModel.likeForumPost(post).observe(this){
            when(it){
                is Resource.Success->{
                    it.data?.let {
                        if(tempPost!=null)viewModel.onViewEvent(ViewEventsForumPost.Edit(tempPost!!,it.first));tempPost=null
                    }
                }
                is Resource.Error->{
                    ToastUtil.makeToast(binding.root.context, it.message.toString())
                    tempPost?.let { it ->
                        viewModel.onViewEvent(ViewEventsForumPost.Rebind(it))
                        tempPost=null
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()


        val layoutManagerForumPost = LinearLayoutManager(this)
        binding.rvForumPost.layoutManager = layoutManagerForumPost


        adapterForum = PagingForumAdapter(onCLick,onCheckChanged,viewModel,this)


        binding.rvForumPost.adapter = adapterForum

        binding.rgKategori.check(
            when (viewModel.mKategoriForum) {
                KategoriForum.SEMUA -> R.id.rb_semua
                KategoriForum.INFORMASI -> R.id.rb_informasi
                KategoriForum.PERTANYAAN -> R.id.rb_pertanyaan
                else -> R.id.rb_lainnya
            }
        )

        binding.rgKategori.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.rb_semua->{
                    if(viewModel.mKategoriForum!= KategoriForum.SEMUA)viewModel.getData(
                    KategoriForum.SEMUA)
                }
                R.id.rb_informasi->{
                    if(viewModel.mKategoriForum!= KategoriForum.INFORMASI)viewModel.getData(
                        KategoriForum.INFORMASI)
                }
                R.id.rb_pertanyaan->{
                    if(viewModel.mKategoriForum!= KategoriForum.PERTANYAAN)viewModel.getData(
                        KategoriForum.PERTANYAAN)
                }
                R.id.rb_lainnya->{
                    if(viewModel.mKategoriForum!= KategoriForum.LAINNYA)viewModel.getData(
                        KategoriForum.LAINNYA)
                }
            }

        }

        binding.btnTopikPostForum.setOnClickListener {
            val topicFragment= ForumTopicFragment()
            topicFragment.show(supportFragmentManager,"topic_dialog")
        }

        lifecycleScope.launch {
            adapterForum.loadStateFlow.collectLatest { loadStates ->
                showLoading(loadStates.refresh is LoadState.Loading)
            }
        }

        viewModel.getData(viewModel.mKategoriForum)
        viewModel.pagingData.observe(this) { it ->
            it.observe(this) {
                if (it != null) {
                    adapterForum.submitData(lifecycle, it)
                }
            }

        }


    }

    private fun showLoading(isShowLoading: Boolean) {
        binding.pbLoading.visibility = if (isShowLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }

    override fun handleDataTopic(data: Topic?) {
        binding.btnTopikPostForum.text= data?.topic_name ?: getString(R.string.semua)
        viewModel.mTopics=data
        viewModel.getData(viewModel.mKategoriForum, data)
    }
}