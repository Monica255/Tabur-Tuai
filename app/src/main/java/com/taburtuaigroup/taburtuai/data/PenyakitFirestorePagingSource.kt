package com.taburtuaigroup.taburtuai.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class PenyakitFirestorePagingSource (
    private val queryPenyakitByTime: Query
) : PagingSource<QuerySnapshot, PenyakitTumbuhan>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, PenyakitTumbuhan>): QuerySnapshot? {
        return null
    }
    var list= mutableListOf<PenyakitTumbuhan>()
    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PenyakitTumbuhan> {
        return try {
            val currentPage = params.key ?: queryPenyakitByTime.get().await()
            var lastVisibleProduct: DocumentSnapshot
            var nextPage:QuerySnapshot?
            if(currentPage.size()!=0){
                lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
                nextPage = queryPenyakitByTime.startAfter(lastVisibleProduct).get().await()
            }else{
                nextPage=null
            }

            list=currentPage.toObjects(PenyakitTumbuhan::class.java)
            LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}