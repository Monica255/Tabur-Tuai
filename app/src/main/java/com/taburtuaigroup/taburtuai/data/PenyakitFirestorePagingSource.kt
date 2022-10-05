package com.taburtuaigroup.taburtuai.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class PenyakitFirestorePagingSource (
    private val queryPenyakitByTime: Query
) : PagingSource<QuerySnapshot, PenyakitTumbuhan>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, PenyakitTumbuhan>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PenyakitTumbuhan> {
        return try {
            val currentPage = params.key ?: queryPenyakitByTime.get().await()
            val lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
            val nextPage = queryPenyakitByTime.startAfter(lastVisibleProduct).get().await()
            LoadResult.Page(
                data = currentPage.toObjects(PenyakitTumbuhan::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}