package com.taburtuaigroup.taburtuai.ui.smartfarming.fav

import androidx.lifecycle.*
import androidx.paging.*
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import com.taburtuaigroup.taburtuai.core.domain.model.Artikel
import com.taburtuaigroup.taburtuai.core.domain.model.PenyakitTumbuhan
import com.taburtuaigroup.taburtuai.core.util.ViewEventsArtikel
import com.taburtuaigroup.taburtuai.core.util.ViewEventsPenyakit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class FavViewModel @Inject constructor(private val smartFarmingUseCase: SmartFarmingUseCase) : ViewModel() {

    fun favoriteArtikel(artikel: Artikel) =
        smartFarmingUseCase.favoriteArtikel(artikel).asLiveData()

    fun favoritePenyakit(penyakit: PenyakitTumbuhan) =
        smartFarmingUseCase.favoritePenyakit(penyakit).asLiveData()


    private val modificationEventsArtikel = MutableStateFlow<List<ViewEventsArtikel>>(emptyList())
    private val modificationEventsPenyakit = MutableStateFlow<List<ViewEventsPenyakit>>(emptyList())

    private val combinedArtikel =smartFarmingUseCase.getPagingArtikel(true)
            .cachedIn(viewModelScope)
            .combine(modificationEventsArtikel) { pagingData, modifications ->
                modifications.fold(pagingData) { acc, event ->
                    applyEventsArtikel(acc, event)
                }
            }

    private val combinedPenyakit =smartFarmingUseCase.getPagingPenyakit(true)
        .cachedIn(viewModelScope)
        .combine(modificationEventsPenyakit) { pagingData, modifications ->
            modifications.fold(pagingData) { acc, event ->
                applyEventsPenyakit(acc, event)
            }
        }

    fun onViewEvent(sampleViewEvents: Any) {
        if(sampleViewEvents is ViewEventsArtikel){
            modificationEventsArtikel.value += sampleViewEvents
        }else if( sampleViewEvents is ViewEventsPenyakit){
            modificationEventsPenyakit.value+= sampleViewEvents
        }

    }

    private fun applyEventsArtikel(
        paging: PagingData<Artikel>,
        ViewEvents: ViewEventsArtikel
    ): PagingData<Artikel> {
        return when (ViewEvents) {
            is ViewEventsArtikel.Remove -> {
                paging
                    .filter { ViewEvents.entity.id_artikel != it.id_artikel }
            }
            is ViewEventsArtikel.Edit -> {
                paging
                    .map {
                        if (ViewEvents.entity.id_artikel == it.id_artikel) return@map it.copy(
                            title = "${it.title} (updated)"
                        )
                        else return@map it
                    }
            }
            is ViewEventsArtikel.Rebind -> {
                paging.map {
                    if (ViewEvents.entity.id_artikel == it.id_artikel) return@map it
                    else return@map it
                }
            }
        }
    }

    private fun applyEventsPenyakit(
        paging: PagingData<PenyakitTumbuhan>,
        ViewEvents: ViewEventsPenyakit
    ): PagingData<PenyakitTumbuhan> {
        return when (ViewEvents) {
            is ViewEventsPenyakit.Remove -> {
                paging
                    .filter { ViewEvents.entity.id_penyakit != it.id_penyakit }
            }
            is ViewEventsPenyakit.Edit -> {
                paging
                    .map {
                        if (ViewEvents.entity.id_penyakit == it.id_penyakit) return@map it.copy(
                            title = "${it.title} (updated)"
                        )
                        else return@map it
                    }
            }
            is ViewEventsPenyakit.Rebind -> {
                paging.map {
                    if (ViewEvents.entity.id_penyakit == it.id_penyakit) return@map it
                    else return@map it
                }
            }
        }
    }

    val pagingArtikelViewStates: LiveData<PagingData<Artikel>> = combinedArtikel.asLiveData()
    val pagingPenyakitViewStates: LiveData<PagingData<PenyakitTumbuhan>> = combinedPenyakit.asLiveData()
}