package com.taburtuaigroup.taburtuai.core.data.source.remote.firebase

import android.content.Context
import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.util.*
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor(
    firebaseStorage: FirebaseStorage,
    firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth,
    firebaseFirestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {
    private val userDataRef = firebaseDatabase.reference.child("user_data/")
    private val storageUserRef = firebaseStorage.reference.child("user_profile")
    private val smartFarmingDataRef = firebaseDatabase.reference.child("smart_farming")
    private val feedbackRef = firebaseDatabase.reference.child("feedbacks")
    private val connectionRef = firebaseDatabase.reference.child("connections")
    private val infoConnected = firebaseDatabase.getReference(".info/connected")
    private val lastConnectedRef = firebaseDatabase.reference.child("lastConnected")
    private val artikelRef = firebaseFirestore.collection("artikel")
    private var penyakitRef = firebaseFirestore.collection("penyakit_tumbuhan")

    private val prefManager =
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)

    val currentUser: FirebaseUser?
        get() {
            return firebaseAuth.currentUser
        }

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    init {
        manageConnection()
    }

    private fun manageConnection() {
        infoConnected.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)
                val userCon = currentUser?.uid?.let {
                    connectionRef.child(it)
                }
                val userLastCon = currentUser?.uid?.let {
                    lastConnectedRef.child(it)
                }

                val serverValue = System.currentTimeMillis()
                if (connected == true) {
                    _isConnected.value = true
                    userLastCon?.setValue("connected")
                    userLastCon?.onDisconnect()?.setValue(serverValue)
                    userCon?.setValue(true)
                    userCon?.onDisconnect()?.let {
                        prefManager.edit().putLong(LAST_UPDATE, serverValue).apply()
                        it.setValue(false)
                    }
                } else if (connected == false) {
                    _isConnected.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "cancel")
            }
        })
    }

    fun isCanSendFeedBack(): Boolean {
        val canSend: Boolean
        val lastDate = prefManager.getString(LAST_DATE_SEND_FEEDBACK, "") ?: ""
        val date = Date()
        val today = DateFormat.format("dd/MM/yyyy", date).toString()
        canSend = if (lastDate == today) {
            val count = prefManager.getInt(FEEDBACK_COUNT, 0)
            count < 3
        } else {
            prefManager.edit().putString(LAST_DATE_SEND_FEEDBACK, "").apply()
            prefManager.edit().putInt(FEEDBACK_COUNT, 0).apply()
            true
        }
        return canSend
    }

    private fun updateCounterFeedback() {
        val date = Date()
        val today = DateFormat.format("dd/MM/yyyy", date).toString()
        prefManager.edit().putString(LAST_DATE_SEND_FEEDBACK, today).apply()
        var count = prefManager.getInt(FEEDBACK_COUNT, 0)
        count += 1
        prefManager.edit().putInt(FEEDBACK_COUNT, count).apply()
    }

    suspend fun kirimMasukan(data: Masukan): Flow<Resource<String>> {
        val key = feedbackRef.push().key
        var msg: Pair<Boolean, String>? = null
        return flow {
            emit(Resource.Loading())
            key?.let { it ->
                feedbackRef.child(it).setValue(data).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //_isLoading.value = false
                        msg =
                            Pair(
                                false,
                                context.resources.getString(R.string.berhasil_kirim_masukan)
                            )
                        updateCounterFeedback()
                    } else {
                        msg =
                            Pair(true, context.resources.getString(R.string.gagal_kirim_masukan))
                    }
                }.await()
                msg?.let {
                    if (!it.first) {
                        emit(Resource.Success(it.second))
                    } else {
                        emit(Resource.Error(it.second))
                    }
                }
            }
        }
    }
    fun favoritePenyakit(penyakit: PenyakitTumbuhan): Flow<Resource<Pair<Boolean, String?>>> {
        return flow {
            emit(Resource.Loading())
            val favorite: Boolean
            val list = penyakit.favorites ?: mutableListOf()
            val s: FieldValue = if (isFavorite(list)) {
                favorite = false
                FieldValue.arrayRemove(currentUser?.uid)
            } else {
                favorite = true
                FieldValue.arrayUnion(currentUser?.uid)
            }
            var successMsg: Pair<Boolean, String?>? = null
            var errorMsg = ""
            penyakitRef.document(penyakit.id_penyakit).update("favorites", s)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        penyakitRef.document(penyakit.id_penyakit)
                            .update("fav_count", FieldValue.increment(if (favorite) 1 else -1))
                        successMsg = Pair(
                            favorite,
                            currentUser?.uid
                        )
                    } else {
                        errorMsg =
                            if (favorite) "Gagal menyimpan penyakit tumbuhan" else "Gagal menghapus penyakit tumbuhan"
                    }
                }.await()
            if (successMsg != null) {
                emit(Resource.Success(successMsg!!))
            } else emit(Resource.Error(errorMsg))
        }
    }

    fun favoriteArtikel(artike: Artikel): Flow<Resource<Pair<Boolean, String?>>> {
        return flow {
            emit(Resource.Loading())
            val favorite: Boolean
            val list = artike.favorites ?: mutableListOf()
            val s: FieldValue = if (isFavorite(list)) {
                favorite = false
                FieldValue.arrayRemove(currentUser?.uid)
            } else {
                favorite = true
                FieldValue.arrayUnion(currentUser?.uid)
            }
            var successMsg: Pair<Boolean, String?>? = null
            var errorMsg = ""
            artikelRef.document(artike.id_artikel).update("favorites", s)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        artikelRef.document(artike.id_artikel)
                            .update("fav_count", FieldValue.increment(if (favorite) 1 else -1))
                        successMsg = Pair(
                            favorite,
                            currentUser?.uid
                        )
                    } else {
                        errorMsg =
                            if (favorite) "Gagal menyimpan artikel" else "Gagal menghapus artikel"
                    }
                }.await()
            if (successMsg != null) {
                emit(Resource.Success(successMsg!!))
            } else emit(Resource.Error(errorMsg))
        }
    }

    private fun isFavorite(list: MutableList<String>): Boolean {
        return list.contains(currentUser?.uid)
    }

    fun getArtikel(idArtikel: String): Flow<Resource<Artikel>> {
        var x: Artikel? = null
        var msg: String = context.getString(R.string.gagal_mendapat_data)
        return flow {
            emit(Resource.Loading())
            artikelRef.whereEqualTo("id_artikel", idArtikel).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        x = it.result.toObjects(Artikel::class.java)[0]
                    } catch (e: Exception) {
                        msg = e.message.toString()
                    }
                } else {
                    msg = context.getString(R.string.gagal_mendapat_data)
                }
            }.await()
            if (x != null) {
                emit(Resource.Success(x!!))
            } else {
                emit(Resource.Error(msg))
            }
        }.flowOn(Dispatchers.IO)

    }

    private fun getQueryArtikelByKategoriAndKeyword(
        kategori: KategoriArtikel,
        limit: Long,
        keyword: String = ""
    ): Query {
        return if (kategori == KategoriArtikel.SEMUA) {
            artikelRef
                .orderBy("title")
                .startAt(keyword)
                .endAt("$keyword~")
                .limit(limit)
        } else {
            artikelRef
                .whereEqualTo("kategori", kategori.printable)
                .orderBy("title")
                .startAt(keyword)
                .endAt("$keyword~")
                .limit(limit)
        }
    }

    fun getPagingArtikel(onlyFav: Boolean): Flow<PagingData<Artikel>> {
        val query: Query = getQueryByTime(Kategori.ARTIKEL, onlyFav, 8)
        val pager = Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                ArtikelFirestorePagingSource(query)
            }
        )
        return pager.flow

    }

    fun getPagingArtikelByKategori(
        kategori: KategoriArtikel,
        keyword: String = ""
    ): LiveData<PagingData<Artikel>> {
        val query: Query = if (keyword.trim() != "") getQueryArtikelByKategoriAndKeyword(
            kategori,
            8,
            keyword
        ) else getQueryByKategori(kategori, 8)
        val pager = Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                ArtikelFirestorePagingSource(query)
            }
        )
        return pager.liveData
    }

    private fun getQueryByFavCount(ref: Kategori, limit: Long): Query {
        val reff = if (ref == Kategori.PENYAKIT) penyakitRef else artikelRef
        return reff.orderBy("fav_count", Query.Direction.DESCENDING)
            .limit(limit)
    }

    fun getArtikelTerpopuler(): Flow<Resource<List<Artikel>>> {
        val query = getQueryByFavCount(Kategori.ARTIKEL, 4)
        return flow {
            val list = mutableListOf<Artikel>()
            var msg: String? = null
            emit(Resource.Loading())
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        for (i in it.result) {
                            val x = i.toObject<Artikel>()
                            list.add(x)
                        }
                    } catch (e: Exception) {
                        msg = e.message
                    }
                } else {
                    msg = context.getString(R.string.gagal_mendapat_data)
                }
            }.await()
            if (msg != null) {
                emit(Resource.Error(msg!!))
            } else {
                emit(Resource.Success(list))
            }
        }

    }

    private fun getQueryByKategori(kategori: KategoriArtikel, limit: Long): Query {
        return if (kategori == KategoriArtikel.SEMUA) {
            artikelRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
        } else {
            artikelRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .whereEqualTo("kategori", kategori.printable)
                .limit(limit)
        }
    }

    fun getListArtikelByKategori(kategori: KategoriArtikel = KategoriArtikel.SEMUA): Flow<Resource<List<Artikel>>> {
        return flow {
            emit(Resource.Loading())
            var list = mutableListOf<Artikel>()
            var msg: String? = null
            val query = getQueryByKategori(kategori, 8)
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        for (i in it.result) {
                            val x = i.toObject<Artikel>()
                            list.add(x)
                        }
                    } catch (e: Exception) {
                        msg = e.message
                        list = mutableListOf()
                    }
                } else {
                    msg = "Error"
                    list = mutableListOf()
                }
            }.await()
            if (msg != null) {
                emit(Resource.Error(msg!!))
            } else {
                emit(Resource.Success(list))
            }
        }

    }

    private fun getQueryByTime(ref: Kategori, onlyFav: Boolean, limit: Long): Query {
        val reff = if (ref == Kategori.PENYAKIT) penyakitRef else artikelRef
        val uid = currentUser?.uid ?: ""
        return if (onlyFav) {
            reff.orderBy("timestamp", Query.Direction.DESCENDING)
                .whereArrayContains("favorites", uid)
                .limit(limit)
        } else {
            reff.orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
        }
    }

    fun getPenyakit(idPenyakit: String) :Flow<Resource<PenyakitTumbuhan>>{
        return flow{
            emit(Resource.Loading())
            var x: PenyakitTumbuhan? = null
            var msg: String = context.getString(R.string.gagal_mendapat_data)
            penyakitRef.whereEqualTo("id_penyakit", idPenyakit).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        x = it.result.toObjects(PenyakitTumbuhan::class.java)[0]
                    } catch (e: Exception) {
                        msg=e.message.toString()
                    }
                } else {
                    msg = context.getString(R.string.gagal_mendapat_data)
                }
            }.await()
            if (x != null) {
                emit(Resource.Success(x!!))
            } else {
                emit(Resource.Error(msg))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getPagingPenyakit(onlyFav: Boolean): Flow<PagingData<PenyakitTumbuhan>> {
        val query: Query = getQueryByTime(Kategori.PENYAKIT, onlyFav, 8)
        val pager = Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                PenyakitFirestorePagingSource(query)
            }
        )
        return pager.flow
    }

    private fun getQueryPenyakitByKeyword(limit: Long, keyword: String = ""): Query {
        return penyakitRef
            .orderBy("title")
            .startAt(keyword)
            .endAt("$keyword~")
            .limit(limit)
    }
    fun getPagingPenyakit(keyword: String = ""): LiveData<PagingData<PenyakitTumbuhan>> {
        val query: Query =
            if (keyword.trim() != "") getQueryPenyakitByKeyword(8, keyword) else getQueryByTime(
                Kategori.PENYAKIT,
                false,
                8
            )
        val pager = Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                PenyakitFirestorePagingSource(query)
            }
        )
        return pager.liveData
    }

    fun getPenyakitTerpopuler():Flow<Resource<List<PenyakitTumbuhan>>> {
        return flow{
            emit(Resource.Loading())
            var list = mutableListOf<PenyakitTumbuhan>()
            var msg: String? = null
            val query = getQueryByFavCount(Kategori.PENYAKIT, 4)
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        for (i in it.result) {
                            val x = i.toObject<PenyakitTumbuhan>()
                            list.add(x)
                        }
                    } catch (e: Exception) {
                        msg=e.message.toString()
                    }
                } else {
                    list = mutableListOf()
                    msg=context.getString(R.string.gagal_mendapat_data)
                }
            }.await()
            if(msg==null){
                emit(Resource.Success(list))
            }else{
                emit(Resource.Error(msg!!))
            }
        }
    }

    fun getListPenyakit(): Flow<Resource<List<PenyakitTumbuhan>>> {
        var list = mutableListOf<PenyakitTumbuhan>()
        var msg: String? = null
        return flow {
            emit(Resource.Loading())
            val query = getQueryByTime(Kategori.PENYAKIT, false, 8)
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        for (i in it.result) {
                            val x = i.toObject<PenyakitTumbuhan>()
                            list.add(x)
                        }
                    } catch (e: Exception) {
                        msg = e.message
                        list = mutableListOf()
                        Log.d("TAG", e.message.toString())
                    }
                } else {
                    msg = "Error"
                    list = mutableListOf()
                }
            }.await()

            if (msg != null) {
                emit(Resource.Error(msg!!))
            } else {
                emit(Resource.Success(list))
            }
        }

    }


    fun updateDeviceState(idKebun: String, idDevice: String, value: Int) {
        currentUser?.uid?.let {
            smartFarmingDataRef.child(it).child("realtime_kebun").child(idKebun)
                .child("controlling")
                .child(idDevice).child("state")
                .setValue(value)
        }
    }

    fun getControllingKebun(idKebun: String): MutableLiveData<Resource<List<Device>>> {
        val realtimeData = MutableLiveData<Resource<List<Device>>>()
        currentUser?.uid?.let { it ->
            realtimeData.value = Resource.Loading()
            smartFarmingDataRef.child(it).child("realtime_kebun").child(idKebun)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val pompa = arrayListOf<Device>()
                        val lampu = arrayListOf<Device>()
                        val sprinkler = arrayListOf<Device>()
                        val drip = arrayListOf<Device>()
                        val fogger = arrayListOf<Device>()
                        val sirine = arrayListOf<Device>()
                        val cctv = arrayListOf<Device>()

                        val devices = arrayListOf<Device>()
                        if (snapshot.exists() && snapshot.key == idKebun) {
                            for (i in snapshot.children) {
                                if (i.key.equals("controlling") && snapshot.key == idKebun) {
                                    for (j in i.children) {
                                        try {
                                            j.getValue(Device::class.java)?.let { it ->
                                                if (it.id_device != "" && j.key.toString() == it.id_device) {
                                                    when (it.type) {
                                                        "pompa" -> pompa.add(it)
                                                        "lampu" -> lampu.add(it)
                                                        "sprinkler" -> sprinkler.add(it)
                                                        "drip" -> drip.add(it)
                                                        "fogger" -> fogger.add(it)
                                                        "sirine" -> sirine.add(it)
                                                        "cctv" -> cctv.add(it)
                                                        else -> devices.add(it)
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            realtimeData.value =
                                                Resource.Error(e.message.toString())
                                        }
                                    }
                                    realtimeData.value = Resource.Success(
                                        pompa.asSequence().plus(lampu).plus(sprinkler).plus(drip)
                                            .plus(fogger)
                                            .plus(sirine).plus(cctv).plus(devices)
                                            .toList()
                                    )
                                }
                            }
                        } else {
                            realtimeData.value =
                                Resource.Error(context.getString(R.string.tidak_ada_data_monitoring))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        realtimeData.value = Resource.Error(error.message)
                    }
                })
        }
        return realtimeData
    }

    fun getMonitoringKebun(idKebun: String): MutableLiveData<Resource<MonitoringKebun>> {
        val realtimeData = MutableLiveData<Resource<MonitoringKebun>>()
        currentUser?.uid?.let { it ->
            realtimeData.value = Resource.Loading()
            smartFarmingDataRef.child(it).child("realtime_kebun").child(idKebun)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && snapshot.key == idKebun) {
                            for (i in snapshot.children) {
                                if (i.key.equals("monitoring")) {
                                    try {
                                        val x = i.getValue(MonitoringKebun::class.java)
                                        realtimeData.value =
                                            if (x != null) Resource.Success(x) else Resource.Error("Data null")
                                    } catch (e: Exception) {
                                        realtimeData.value = Resource.Error(e.message.toString())
                                    }
                                }
                            }
                        } else {
                            realtimeData.value =
                                Resource.Error(context.getString(R.string.tidak_ada_data_monitoring))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        realtimeData.value = Resource.Error(error.message)
                    }
                })
        }
        return realtimeData
    }

    fun getKebun(idKebun: String): MutableLiveData<Resource<Kebun?>> {
        val kebun = MutableLiveData<Resource<Kebun?>>()
        currentUser?.uid?.let {
            kebun.value = Resource.Loading()
            smartFarmingDataRef.child(it).child("kebun").child(idKebun)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && snapshot.key == idKebun) {
                            try {
                                val x = snapshot.getValue(Kebun::class.java)
                                if (x != null) {
                                    kebun.value = Resource.Success(x)
                                } else {
                                    kebun.value = Resource.Error("Data kebun tidak ada")
                                }
                            } catch (e: Exception) {
                                kebun.value = Resource.Error(e.message.toString())
                            }
                        } else {
                            kebun.value = Resource.Error("Data kebun tidak ada")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        kebun.value = Resource.Error(error.message)
                    }
                })
        }
        return kebun
    }

    fun getAllKebunPetani(
        idPetani: String,
        kebunName: String = ""
    ): MutableLiveData<Resource<List<Kebun>>> {
        val allKebunPetani = MutableLiveData<Resource<List<Kebun>>>()
        val queryAll = currentUser?.uid?.let {
            smartFarmingDataRef.child(it).child("kebun").orderByChild("id_petani")
                .equalTo(idPetani)
        }
        allKebunPetani.value = Resource.Loading()
        queryAll?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = arrayListOf<Kebun>()
                if (snapshot.exists()) {
                    for (i in snapshot.children) {
                        try {
                            val kebun = i.getValue(Kebun::class.java)
                            kebun?.let {
                                if (it.id_kebun != "" && i.key.toString() == it.id_kebun) {
                                    if (kebunName != "") {
                                        if (it.nama_kebun.contains(kebunName, true)) {
                                            list.add(kebun)
                                        }
                                    } else {
                                        list.add(it)
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            allKebunPetani.value =
                                Resource.Error(e.message.toString())
                        }
                    }
                    allKebunPetani.value = Resource.Success(list)
                } else {
                    allKebunPetani.value =
                        Resource.Error(context.resources.getString(R.string.tidak_ada_kebun))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                allKebunPetani.value =
                    Resource.Error(context.resources.getString(R.string.tidak_ada_kebun))
                Log.d("TAG", error.message)
            }
        })
        return allKebunPetani
    }

    fun loginPetani(
        idPetani: String,
        passPetani: String? = null
    ): MutableLiveData<Resource<Petani?>> {
        val petani = MutableLiveData<Resource<Petani?>>()
        val query = currentUser?.uid?.let {
            smartFarmingDataRef.child(it).child("petani").child(idPetani)
        }
        /*if (_isConnected.value == false) {
            _isLoading.value = false
        } else if (_isConnected.value == true) {
            _isLoading.value = true
        }*/
        petani.value = Resource.Loading()
        query?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (passPetani != null) {
                        first@ for (i in snapshot.children) {
                            if (i.key.equals("password_petani")) {
                                if (i.value == passPetani) {
                                    prefManager.edit().putString(SESI_PETANI_ID, idPetani).apply()
                                    try {
                                        petani.value =
                                            Resource.Success(snapshot.getValue(Petani::class.java))
                                    } catch (e: Exception) {
                                        petani.value = Resource.Error(e.message.toString())
                                    }
                                } else {
                                    prefManager.edit().putString(SESI_PETANI_ID, "").apply()
                                    petani.value =
                                        Resource.Error(context.resources.getString(R.string.wrong_password))
                                }
                                break@first
                            }
                        }
                    } else {
                        try {
                            petani.value = Resource.Success(snapshot.getValue(Petani::class.java))
                        } catch (e: Exception) {
                            petani.value = Resource.Error(e.message.toString())
                        }
                    }
                } else {
                    petani.value =
                        Resource.Error(context.resources.getString(R.string.id_petani_not_found))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                petani.value = Resource.Error(context.resources.getString(R.string.error))
            }
        })
        return petani

    }

    fun getAllKebun(kebunName: String = ""): MutableLiveData<Resource<List<Kebun>>> {
        val allKebun = MutableLiveData<Resource<List<Kebun>>>()
        currentUser?.uid?.let { it ->
            allKebun.value = Resource.Loading()
            smartFarmingDataRef.child(it).child("kebun")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = ArrayList<Kebun>()
                        if (snapshot.exists()) {
                            for (i in snapshot.children) {
                                try {
                                    val kebun = i.getValue(Kebun::class.java)
                                    kebun?.let { it ->
                                        if (it.id_kebun != "" && i.key.toString() == it.id_kebun) {
                                            if (kebunName != "") {
                                                if (it.nama_kebun.contains(kebunName, true)) {
                                                    list.add(kebun)
                                                }
                                            } else {
                                                list.add(it)
                                            }
                                        }
                                    }

                                } catch (e: Exception) {
                                    allKebun.value = Resource.Error(e.message.toString())
                                }
                            }
                            allKebun.value = Resource.Success(list)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        allKebun.value = Resource.Error(error.message)
                    }
                })
        }
        return allKebun
    }

    fun getAllPetani(petaniName: String = ""): MutableLiveData<Resource<List<Petani>>> {
        val allPetani = MutableLiveData<Resource<List<Petani>>>()
        currentUser?.uid?.let { it ->
            allPetani.value = Resource.Loading()
            smartFarmingDataRef.child(it).child("petani")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = ArrayList<Petani>()
                        if (snapshot.exists()) {
                            for (i in snapshot.children) {
                                try {
                                    val petani = i.getValue(Petani::class.java)
                                    petani?.let {
                                        if (it.id_petani != "" && i.key.toString() == it.id_petani) {
                                            if (petaniName != "") {
                                                if (it.nama_petani.contains(petaniName, true)) {
                                                    list.add(petani)
                                                }
                                            } else {
                                                list.add(it)
                                            }
                                        }
                                    }

                                } catch (e: Exception) {
                                    allPetani.value = Resource.Error(e.message.toString())
                                }
                            }
                            allPetani.value = Resource.Success(list)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("TAG", error.message)
                        allPetani.value = Resource.Error(error.message)
                    }
                })
        }
        return allPetani

    }


    suspend fun updateUserData(data: UserData): Flow<Resource<String>> {
        var msg: Pair<Boolean, String>? = null
        return flow {
            emit(Resource.Loading())
            currentUser?.uid?.let { it ->
                userDataRef.child(it).setValue(data).addOnCompleteListener {
                    msg = if (it.isSuccessful) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(data.name)
                            .build()
                        currentUser?.updateProfile(profileUpdates)
                        Pair(false, context.getString(R.string.berhasil_ubah_data))
                    } else {
                        Pair(true, context.getString(R.string.gagal_ubah_data))
                    }
                }.await()
                msg?.let {
                    if (!it.first) {
                        emit(Resource.Success(it.second))
                    } else {
                        emit(Resource.Error(it.second))
                    }
                }
            }
        }
    }

    suspend fun uploadProfilePicture(filePath: Uri): Flow<Resource<String>> {
        var msg: Pair<Boolean, String>?
        return flow<Resource<String>> {
            emit(Resource.Loading())
            currentUser?.uid?.let { uid ->
                val r = storageUserRef.child(uid).putFile(filePath).await()
                if (r.task.isSuccessful) {
                    msg = Pair(
                        false,
                        context.getString(R.string.berhasil_ubah_data)
                    )
                    storageUserRef.child(uid).downloadUrl.addOnCompleteListener { uri ->
                        userDataRef.child(uid).child("img_profile")
                            .setValue(uri.result.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val profileUpdates =
                                        UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri.result)
                                            .build()
                                    currentUser?.updateProfile(
                                        profileUpdates
                                    )
                                } else {
                                    // todo
                                }
                            }
                    }
                } else {
                    msg =
                        Pair(true, context.getString(R.string.gagal_ubah_data))
                }
                msg?.let {
                    if (!it.first) {
                        emit(Resource.Success(it.second))
                    } else {
                        emit(Resource.Error(it.second))
                    }
                }
            }

        }.flowOn(Dispatchers.IO)

    }

    fun getUserData(): MutableLiveData<UserData?> {
        val userData = MutableLiveData<UserData?>()
        currentUser?.uid?.let {
            userDataRef.child(it)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            try {
                                val user = snapshot.getValue(UserData::class.java)
                                userData.value = user
                            } catch (e: Exception) {
                                userData.value = null
                                Log.d("TAG", e.message.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        userData.value = null
                        Log.d("TAG", error.toString())
                    }

                })
        }
        return userData
    }

    fun login(email: String, pass: String): Flow<Resource<String>> {
        return flow {
            try {
                emit(Resource.Loading())
                val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
                result?.let {

                    if (result.user != null) {
                        emit(Resource.Success(context.resources.getString(R.string.msg_login_successfully)))
                    } else {
                        emit(Resource.Error(context.resources.getString(R.string.msg_email_pass_might_be_wrong)))
                    }
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
                Log.e("TAG", e.toString())
            }
        }
    }

    suspend fun firebaseAuthWithGoogle(idToken: String): Flow<Resource<String>> {
        return flow {
            try {
                emit(Resource.Loading())
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()
                result?.let {
                    if (result.user != null) {
                        val x = setUserData()
                        emit(Resource.Success(x.second))
                    } else {
                        emit(Resource.Error<String>("Error"))
                    }
                } ?: emit(Resource.Error<String>("Error"))

            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
                Log.e("TAG", e.toString())
            }

        }.flowOn(Dispatchers.IO)
    }

    suspend fun registerAccount(
        email: String,
        pass: String,
        name: String,
        telepon: String,
    ): Flow<Resource<String>> {
        return flow {
            try {
                emit(Resource.Loading())
                val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
                if (result.user != null) {
                    val x = setUserData(email, name, telepon)
                    emit(Resource.Success(x.second))
                } else {
                    emit(Resource.Error<String>("Error"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.toString()))
                Log.e("TAG", e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }


    private suspend fun setUserData(
        email: String = "",
        name: String = "",
        telepon: String = ""
    ): Pair<Boolean, String> {
        if (name != "") {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            currentUser?.updateProfile(profileUpdates)
        }

        val photo = if (currentUser?.photoUrl != null) currentUser?.photoUrl.toString() else ""
        val user = UserData(
            currentUser?.email ?: email,
            currentUser?.displayName ?: name,
            telepon,
            currentUser?.uid ?: "",
            photo
        )
        Log.d("TAG", "ud " + user)
        var result: Pair<Boolean, String> = Pair(true, "Gagal masuk")
        currentUser?.uid?.let { it ->
            userDataRef.child(it).setValue(user).addOnCompleteListener { v ->
                result = if (v.isSuccessful) {
                    if (name != "") {
                        Pair(false, "Berhasil masuk")
                    } else {
                        Pair(false, "Berhasil masuk dengan akun google")
                    }
                } else {
                    Pair(true, v.exception?.message.toString()).also {
                        currentUser?.delete()
                        signOut()
                    }
                }
            }.await()
        }
        return result
    }

    fun signOut() {
        if (currentUser != null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.resources.getString(R.string.default_web_client_id_2))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInClient.signOut()
            prefManager.edit().putString(SESI_PETANI_ID, "").apply()
            firebaseAuth.signOut()
        }
    }


}