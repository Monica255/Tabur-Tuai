package com.taburtuaigroup.taburtuai.core.domain.usecase

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.repository.ISmartFarmingRepository
import com.taburtuaigroup.taburtuai.core.util.KategoriArtikel
import com.taburtuaigroup.taburtuai.core.domain.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SmartFarmingInteractor @Inject constructor(
    private val repo: ISmartFarmingRepository
) : SmartFarmingUseCase {
    override val currentUser: FirebaseUser?=repo.currentUser
    override val isConnected: LiveData<Boolean> = repo.isConnected
    override fun getListPenyakit(): Flow<Resource<List<PenyakitTumbuhan>>> = repo.getListPenyakit()
    override fun getPenyakitTerpopuler(): Flow<Resource<List<PenyakitTumbuhan>>> =
        repo.getPenyakitTerpopuler()

    override fun getPagingPenyakit(keyword: String): LiveData<PagingData<PenyakitTumbuhan>> =
        repo.getPagingPenyakit(keyword)

    override fun getPagingPenyakit(onlyFav: Boolean): Flow<PagingData<PenyakitTumbuhan>> =
        repo.getPagingPenyakit(onlyFav)

    override fun getPenyakit(idPenyakit: String): Flow<Resource<PenyakitTumbuhan>> =
        repo.getPenyakit(idPenyakit)

    override fun favoritePenyakit(penyakit: PenyakitTumbuhan): Flow<Resource<Pair<Boolean, String?>>> =
        repo.favoritePenyakit(penyakit)

    override fun getListArtikelByKategori(kategoriArtikel: KategoriArtikel): Flow<Resource<List<Artikel>>> =
        repo.getListArtikelByKategori(kategoriArtikel)

    override fun getArtikelTerpopuler(): Flow<Resource<List<Artikel>>> = repo.getArtikelTerpopuler()
    override fun getPagingArtikelByKategori(
        kategori: KategoriArtikel,
        keyword: String
    ): LiveData<PagingData<Artikel>> = repo.getPagingArtikelByKategori(kategori, keyword)

    override fun getPagingArtikel(onlyFav: Boolean): Flow<PagingData<Artikel>> =
        repo.getPagingArtikel(onlyFav)

    override fun getArtikel(idArtikel: String): Flow<Resource<Artikel>> = repo.getArtikel(idArtikel)
    override fun favoriteArtikel(artike: Artikel): Flow<Resource<Pair<Boolean, String?>>> =
        repo.favoriteArtikel(artike)

    override fun getScheduler(
        idPetani: String,
        onlyActive: Boolean
    ): MutableLiveData<Resource<List<Mscheduler>>> =repo.getScheduler(idPetani, onlyActive)

    override fun updateScheduleData(
        mScheduler: Mscheduler,
        status: Boolean,
        context: Context
    ): Flow<Resource<Boolean>> = repo.updateScheduleData(mScheduler,status, context)

    override fun deleteScheduler(mScheduler: Mscheduler): Flow<Resource<String>> = repo.deleteScheduler(mScheduler)
    override fun addScheduler(mScheduler: Mscheduler): Flow<Resource<String>> = repo.addScheduler(mScheduler)


    override fun setSelaluLoginPetani(isAlwaysLogin: Boolean) {
        repo.setSelaluLoginPetani(isAlwaysLogin)
    }

    override fun getAllPetani(petaniName: String): MutableLiveData<Resource<List<Petani>>> =
        repo.getAllPetani(petaniName)

    override fun getAllKebun(kebunName: String): MutableLiveData<Resource<List<Kebun>>> =
        repo.getAllKebun(kebunName)

    override fun loginPetani(
        idPetani: String,
        passPetani: String?
    ): MutableLiveData<Resource<Petani?>> = repo.loginPetani(idPetani, passPetani)

    override fun getAllKebunPetani(
        idPetani: String,
        kebunName: String
    ): MutableLiveData<Resource<List<Kebun>>> = repo.getAllKebunPetani(idPetani, kebunName)

    override fun getKebun(idKebun: String): MutableLiveData<Resource<Kebun?>> =
        repo.getKebun(idKebun)

    override fun getMonitoringKebun(idKebun: String): MutableLiveData<Resource<MonitoringKebun>> =
        repo.getMonitoringKebun(idKebun)

    override fun getControllingKebun(idKebun: String): MutableLiveData<Resource<List<Device>>> =
        repo.getControllingKebun(idKebun)

    override fun updateDeviceState(idKebun: String, idDevice: String, value: Int) : Flow<Resource<String>> =
        repo.updateDeviceState(idKebun, idDevice, value)

}