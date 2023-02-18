package com.taburtuaigroup.taburtuai.core.data

/*
import com.taburtuaigroup.taburtuai.core.data.source.remote.network.ApiResponse
import kotlinx.coroutines.flow.*

abstract class NetworkBoundResource<ResultType, RequestType> {

    private var result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        val data = query().first()
        if (shouldFetch(data)) {
            emit(Resource.Loading())
            when (val apiResponse = fetch().first()) {
                is ApiResponse.Success -> {
                    saveFetchResult(apiResponse.data)
                    emitAll(query().map { Resource.Success(it) })
                }
                is ApiResponse.Empty -> {
                    emitAll(query().map { Resource.Success(it) })
                }
                is ApiResponse.Error -> {
                    onFetchFailed()
                    emit(Resource.Error(apiResponse.errorMessage))
                }
            }
        } else {
            emitAll(query().map { Resource.Success(it) })
        }
    }

    abstract fun query(): Flow<ResultType>

    abstract suspend fun fetch(): Flow<ApiResponse<RequestType>>

    abstract suspend fun saveFetchResult(data: RequestType)

    open fun onFetchFailed() = Unit

    open fun shouldFetch(data: ResultType) = true

    fun asFlow(): Flow<Resource<ResultType>> = result
}*/
