package com.taburtuaigroup.taburtuai.core.data.source.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseUser
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.data.source.remote.firebase.FirebaseDataSource
import com.taburtuaigroup.taburtuai.core.domain.model.ForumPost
import com.taburtuaigroup.taburtuai.core.domain.model.Topic
import com.taburtuaigroup.taburtuai.core.domain.repository.IForumRepository
import com.taburtuaigroup.taburtuai.core.util.KategoriForum
import com.taburtuaigroup.taburtuai.core.util.KategoriTopik
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ForumRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
):IForumRepository {
    override val currentUser: FirebaseUser?=firebaseDataSource.currentUser
    override fun getPagingForumByKategori(kategori: KategoriForum,topic: Topic?): Flow<PagingData<ForumPost>> =firebaseDataSource.getPagingForumByKategori(kategori,topic)
    override fun getListTopikForum(kategory: KategoriTopik): Flow<Resource<List<Topic>>> =firebaseDataSource.getListTopikForum(kategory)
    override fun likeForumPost(forumPost: ForumPost): Flow<Resource<Pair<Boolean, String?>>> =firebaseDataSource.likeForumPost(forumPost)
}