package com.taburtuaigroup.taburtuai.core.domain.usecase

import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.repository.IFeedBackRepository
import com.taburtuaigroup.taburtuai.core.domain.model.Masukan
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedBackInteractor @Inject constructor(private val repo:IFeedBackRepository) :FeedBackUseCase{
    override fun isCanSendFeedBack(): Boolean = repo.isCanSendFeedBack()

    override suspend fun kirimMasukan(data: Masukan): Flow<Resource<String>> = repo.kirimMasukan(data)
}