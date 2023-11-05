package com.taburtuaigroup.taburtuai.core.data.source.remote.firebase

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Query
import com.taburtuaigroup.taburtuai.core.domain.model.Artikel
import com.taburtuaigroup.taburtuai.core.domain.model.ForumPost
import kotlinx.coroutines.tasks.await

class ForumFirestorePagingSource (
    private val query: Query
) : PagingSource<QuerySnapshot, ForumPost>() {
    var list= mutableListOf<ForumPost>()
    override fun getRefreshKey(state: PagingState<QuerySnapshot, ForumPost>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, ForumPost> {
        return try {
            val currentPage = params.key ?: query.get().await()
            var lastVisibleProduct:DocumentSnapshot
            var nextPage:QuerySnapshot?
            if(currentPage.size()!=0){
                lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
                nextPage = query.startAfter(lastVisibleProduct).get().await()
            }else{
                nextPage=null
            }

            //list= mutableListOf()
            list=currentPage.toObjects(ForumPost::class.java)
            LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            Log.d("TAG","error "+e.message.toString())
            LoadResult.Error(e)
        }
    }
}