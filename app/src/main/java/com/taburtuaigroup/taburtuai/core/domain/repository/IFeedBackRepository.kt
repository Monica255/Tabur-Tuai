package com.taburtuaigroup.taburtuai.core.domain.repository

import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Masukan
import kotlinx.coroutines.flow.Flow

interface IFeedBackRepository {
    suspend fun kirimMasukan(data: Masukan): Flow<Resource<String>>

    fun isCanSendFeedBack(): Boolean
}