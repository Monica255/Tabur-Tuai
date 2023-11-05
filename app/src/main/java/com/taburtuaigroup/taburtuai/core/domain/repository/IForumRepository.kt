package com.taburtuaigroup.taburtuai.core.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseUser
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Artikel
import com.taburtuaigroup.taburtuai.core.domain.model.ForumPost
import com.taburtuaigroup.taburtuai.core.domain.model.Topic
import com.taburtuaigroup.taburtuai.core.util.KategoriArtikel
import com.taburtuaigroup.taburtuai.core.util.KategoriForum
import com.taburtuaigroup.taburtuai.core.util.KategoriTopik
import kotlinx.coroutines.flow.Flow

interface IForumRepository {
    val currentUser:FirebaseUser?
    fun getPagingForumByKategori(
        kategori: KategoriForum,
        topic: Topic?
    ): Flow<PagingData<ForumPost>>

    fun getListTopikForum(kategory: KategoriTopik): Flow<Resource<List<Topic>>>

    fun likeForumPost(forumPost: ForumPost): Flow<Resource<Pair<Boolean, String?>>>
}