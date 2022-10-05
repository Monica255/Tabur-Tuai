package com.taburtuaigroup.taburtuai.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ArtikelFirestorePagingSource (
    private val queryArtikelByTime: Query
) : PagingSource<QuerySnapshot, Artikel>() {
    var list= mutableListOf<Artikel>()
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Artikel>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Artikel> {
        return try {
            Log.d("TAG","aa")
            val currentPage = params.key ?: queryArtikelByTime.get().await()
            var lastVisibleProduct:DocumentSnapshot
            var nextPage:QuerySnapshot?
            if(currentPage.size()!=0){
                lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
                nextPage = queryArtikelByTime.startAfter(lastVisibleProduct).get().await()
            }else{
                nextPage=null
            }

            //list= mutableListOf()
            list=currentPage.toObjects(Artikel::class.java)
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