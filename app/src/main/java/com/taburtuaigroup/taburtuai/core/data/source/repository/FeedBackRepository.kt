package com.taburtuaigroup.taburtuai.core.data.source.repository

import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.data.source.remote.firebase.FirebaseDataSource
import com.taburtuaigroup.taburtuai.core.domain.repository.IFeedBackRepository
import com.taburtuaigroup.taburtuai.core.domain.model.Masukan
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedBackRepository @Inject constructor(private val firebaseDataSource: FirebaseDataSource):IFeedBackRepository {
    override suspend fun kirimMasukan(data: Masukan): Flow<Resource<String>> = firebaseDataSource.kirimMasukan(data)

    override fun isCanSendFeedBack(): Boolean = firebaseDataSource.isCanSendFeedBack()
}