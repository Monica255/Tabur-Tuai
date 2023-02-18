package com.taburtuaigroup.taburtuai.core.domain.usecase

import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Masukan
import kotlinx.coroutines.flow.Flow

interface FeedBackUseCase {
    fun isCanSendFeedBack(): Boolean

    suspend fun kirimMasukan(data: Masukan): Flow<Resource<String>>
}