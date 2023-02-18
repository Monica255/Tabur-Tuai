package com.taburtuaigroup.taburtuai.core.data.source.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.data.source.remote.firebase.FirebaseDataSource
import com.taburtuaigroup.taburtuai.core.domain.repository.ISmartFarmingRepository
import com.taburtuaigroup.taburtuai.core.util.IS_ALWAYS_LOGIN_PETANI
import com.taburtuaigroup.taburtuai.core.util.KategoriArtikel
import com.taburtuaigroup.taburtuai.core.util.SESI_PETANI_ID
import com.taburtuaigroup.taburtuai.core.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartFarmingRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource,
    @ApplicationContext appContext: Context
) :
    ISmartFarmingRepository {
    override val isConnected: LiveData<Boolean> = firebaseDataSource.isConnected
    override fun getListPenyakit(): Flow<Resource<List<PenyakitTumbuhan>>> =
        firebaseDataSource.getListPenyakit()

    override fun getPenyakitTerpopuler(): Flow<Resource<List<PenyakitTumbuhan>>> =
        firebaseDataSource.getPenyakitTerpopuler()

    override fun getPagingPenyakit(keyword: String): LiveData<PagingData<PenyakitTumbuhan>> =
        firebaseDataSource.getPagingPenyakit(keyword)

    override fun getPagingPenyakit(onlyFav: Boolean): Flow<PagingData<PenyakitTumbuhan>> =
        firebaseDataSource.getPagingPenyakit(onlyFav)

    override fun getPenyakit(idPenyakit: String): Flow<Resource<PenyakitTumbuhan>> =
        firebaseDataSource.getPenyakit(idPenyakit)

    override fun favoritePenyakit(penyakit: PenyakitTumbuhan): Flow<Resource<Pair<Boolean, String?>>> =
        firebaseDataSource.favoritePenyakit(penyakit)

    override fun getListArtikelByKategori(kategoriArtikel: KategoriArtikel): Flow<Resource<List<Artikel>>> =
        firebaseDataSource.getListArtikelByKategori(kategoriArtikel)

    override fun getArtikelTerpopuler(): Flow<Resource<List<Artikel>>> =
        firebaseDataSource.getArtikelTerpopuler()

    override fun getPagingArtikelByKategori(
        kategori: KategoriArtikel,
        keyword: String
    ): LiveData<PagingData<Artikel>> =
        firebaseDataSource.getPagingArtikelByKategori(kategori, keyword)

    override fun getPagingArtikel(onlyFav: Boolean): Flow<PagingData<Artikel>> =
        firebaseDataSource.getPagingArtikel(onlyFav)

    override fun getArtikel(idArtikel: String): Flow<Resource<Artikel>> =
        firebaseDataSource.getArtikel(idArtikel)

    override fun favoriteArtikel(artike: Artikel): Flow<Resource<Pair<Boolean, String?>>> =
        firebaseDataSource.favoriteArtikel(artike)

    val prefManager =
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(appContext)

    override fun setSelaluLoginPetani(isAlwaysLogin: Boolean) {
        if (isAlwaysLogin) {
            prefManager.edit().putBoolean(IS_ALWAYS_LOGIN_PETANI, true).apply()
        } else {
            //logoutPetani()
            prefManager.edit().putString(SESI_PETANI_ID, "").apply()
            prefManager.edit().putBoolean(IS_ALWAYS_LOGIN_PETANI, false).apply()
        }
    }

    override fun getAllPetani(petaniName: String): MutableLiveData<Resource<List<Petani>>> =
        firebaseDataSource.getAllPetani(petaniName)

    override fun getAllKebun(kebunName: String): MutableLiveData<Resource<List<Kebun>>> =
        firebaseDataSource.getAllKebun(kebunName)

    override fun loginPetani(
        idPetani: String,
        passPetani: String?
    ): MutableLiveData<Resource<Petani?>> = firebaseDataSource.loginPetani(idPetani, passPetani)

    override fun getAllKebunPetani(
        idPetani: String,
        kebunName: String
    ): MutableLiveData<Resource<List<Kebun>>> =
        firebaseDataSource.getAllKebunPetani(idPetani, kebunName)

    override fun getKebun(idKebun: String): MutableLiveData<Resource<Kebun?>> =
        firebaseDataSource.getKebun(idKebun)

    override fun getMonitoringKebun(idKebun: String): MutableLiveData<Resource<MonitoringKebun>> =
        firebaseDataSource.getMonitoringKebun(idKebun)

    override fun getControllingKebun(idKebun: String): MutableLiveData<Resource<List<Device>>> =
        firebaseDataSource.getControllingKebun(idKebun)

    override fun updateDeviceState(idKebun: String, idDevice: String, value: Int) =
        firebaseDataSource.updateDeviceState(idKebun, idDevice, value)
}