package com.taburtuaigroup.taburtuai.core.domain.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.util.KategoriArtikel
import com.taburtuaigroup.taburtuai.core.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ISmartFarmingRepository {
    val currentUser:FirebaseUser?
    fun setSelaluLoginPetani(isAlwaysLogin: Boolean)

    fun getAllPetani(petaniName: String = ""): MutableLiveData<Resource<List<Petani>>>

    fun getAllKebun(kebunName: String = ""):MutableLiveData<Resource<List<Kebun>>>

    fun loginPetani(idPetani: String, passPetani: String?): MutableLiveData<Resource<Petani?>>

    fun getAllKebunPetani(
        idPetani: String,
        kebunName: String = ""
    ): MutableLiveData<Resource<List<Kebun>>>

    fun getKebun(idKebun: String):MutableLiveData<Resource<Kebun?>>

    fun getMonitoringKebun(idKebun: String):MutableLiveData<Resource<MonitoringKebun>>

    fun getControllingKebun(idKebun: String): MutableLiveData<Resource<List<Device>>>

    fun updateDeviceState(idKebun: String, idDevice: String, value: Int): Flow<Resource<String>>
    val isConnected:LiveData<Boolean>

    fun getListPenyakit() :Flow<Resource<List<PenyakitTumbuhan>>>

    fun getPenyakitTerpopuler():Flow<Resource<List<PenyakitTumbuhan>>>

    fun getPagingPenyakit(keyword: String = ""): LiveData<PagingData<PenyakitTumbuhan>>

    fun getPagingPenyakit(onlyFav: Boolean): Flow<PagingData<PenyakitTumbuhan>>

    fun getPenyakit(idPenyakit: String) :Flow<Resource<PenyakitTumbuhan>>

    fun favoritePenyakit(penyakit: PenyakitTumbuhan): Flow<Resource<Pair<Boolean, String?>>>

    fun getListArtikelByKategori(kategoriArtikel: KategoriArtikel) :Flow<Resource<List<Artikel>>>

    fun getArtikelTerpopuler() :Flow<Resource<List<Artikel>>>

    fun getPagingArtikelByKategori(
        kategori: KategoriArtikel,
        keyword: String = ""
    ): LiveData<PagingData<Artikel>>

    fun getPagingArtikel(onlyFav: Boolean): Flow<PagingData<Artikel>>

    fun getArtikel(idArtikel: String):Flow<Resource<Artikel>>

    fun favoriteArtikel(artike: Artikel): Flow<Resource<Pair<Boolean, String?>>>

    fun getScheduler(idPetani: String, onlyActive: Boolean): MutableLiveData<Resource<List<Mscheduler>>>

    fun updateScheduleData(
        mScheduler: Mscheduler,
        status: Boolean,
        context: Context
    ): Flow<Resource<Boolean>>

    fun deleteScheduler(
        mScheduler: Mscheduler,
    ): Flow<Resource<String>>

    fun addScheduler(
        mScheduler: Mscheduler,
    ): Flow<Resource<String>>
}