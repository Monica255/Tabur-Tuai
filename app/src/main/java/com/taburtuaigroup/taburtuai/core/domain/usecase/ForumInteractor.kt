package com.taburtuaigroup.taburtuai.core.domain.usecase

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseUser
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.ForumPost
import com.taburtuaigroup.taburtuai.core.domain.model.Topic
import com.taburtuaigroup.taburtuai.core.domain.repository.IForumRepository
import com.taburtuaigroup.taburtuai.core.util.KategoriForum
import com.taburtuaigroup.taburtuai.core.util.KategoriTopik
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ForumInteractor @Inject constructor(
    private val repo:IForumRepository
) :ForumUseCase{
    override val currentUser: FirebaseUser?=repo.currentUser

    override fun getPagingForumByKategori(kategori: KategoriForum,topic: Topic?): Flow<PagingData<ForumPost>> =repo.getPagingForumByKategori(kategori,topic)

    override fun getListTopikForum(kategory: KategoriTopik): Flow<Resource<List<Topic>>> =repo.getListTopikForum(kategory)

    override fun likeForumPost(forumPost: ForumPost): Flow<Resource<Pair<Boolean, String?>>> =repo.likeForumPost(forumPost)

}