package com.taburtuaigroup.taburtuai.ui.komunitas.forum

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.ForumPost
import com.taburtuaigroup.taburtuai.core.domain.model.Topic
import com.taburtuaigroup.taburtuai.core.domain.usecase.AuthUseCase
import com.taburtuaigroup.taburtuai.core.domain.usecase.ForumUseCase
import com.taburtuaigroup.taburtuai.core.util.KategoriForum
import com.taburtuaigroup.taburtuai.core.util.KategoriTopik
import com.taburtuaigroup.taburtuai.core.util.ViewEventsForumPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val forumUseCase: ForumUseCase,
    private val authUseCase: AuthUseCase
) : ViewModel() {
    var mKategoriForum = KategoriForum.SEMUA
    var mTopics: Topic? = null
    val currentUser = forumUseCase.currentUser

    fun getUserdata(uid: String?) = authUseCase.getUserData(uid)

    fun likeForumPost(forumPost: ForumPost) = forumUseCase.likeForumPost(forumPost).asLiveData()

    private lateinit var modificationEventsForumPost :MutableStateFlow<List<ViewEventsForumPost>>

    //Paging
    val pagingData = MutableLiveData<LiveData<PagingData<ForumPost>>>()

    fun getData(kategoriForum: KategoriForum = mKategoriForum, topic: Topic? = mTopics) {
        if (mKategoriForum != kategoriForum) mKategoriForum = kategoriForum
        if (mTopics != topic) mTopics = topic
        modificationEventsForumPost=MutableStateFlow(emptyList())
        pagingData.value = forumUseCase.getPagingForumByKategori(mKategoriForum, mTopics)
            .cachedIn(viewModelScope)
            .combine(modificationEventsForumPost) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    //Log.d("TAG","modf "+modifications.toString())
                    applyEventsForumPost(acc, event)
                }
            }.asLiveData()
    }

    fun getListTopik(kategori: KategoriTopik): LiveData<Resource<List<Topic>>> =
        forumUseCase.getListTopikForum(kategori).asLiveData()


    fun onViewEvent(sampleViewEvents: ViewEventsForumPost) {
        modificationEventsForumPost.value += sampleViewEvents
    }

    private fun applyEventsForumPost(
        paging: PagingData<ForumPost>,
        ViewEvents: ViewEventsForumPost
    ): PagingData<ForumPost> {
        return when (ViewEvents) {
            is ViewEventsForumPost.Remove -> {
                paging
                    .filter { ViewEvents.entity.id_forum_post != it.id_forum_post }
            }
            is ViewEventsForumPost.Edit -> {
                //Log.d("TAG", "ve " + ViewEvents.isLiked.toString())
                paging
                    .map {
                        if (ViewEvents.entity.id_forum_post == it.id_forum_post) return@map it.copy(
                            like_count = if (ViewEvents.isLiked) it.like_count + 1 else it.like_count - 1,
                            likes = if (currentUser != null) {
                                var list = mutableListOf<String>()
                                if(ViewEvents.isLiked){
                                    list.add(currentUser.uid)
                                }else list.remove(currentUser.uid)
                                list
                            } else it.likes
                        )
                        else return@map it
                    }
            }
            is ViewEventsForumPost.Rebind -> {
                paging.map {
                    if (ViewEvents.entity.id_forum_post == it.id_forum_post) return@map it
                    else return@map it
                }
            }
        }
    }
}