package com.taburtuaigroup.taburtuai.data

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.data.retrofit.ApiConfig
import com.taburtuaigroup.taburtuai.util.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Repository(
    private val context: Application,
    private val mDb: FirebaseDatabase,
    private val mFb: FirebaseFirestore
) {

    //private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private var mAuth: FirebaseAuth? = null
    //private var mDb: FirebaseDatabase? = null

    private var connectionRef: DatabaseReference
    private var infoConnected: DatabaseReference
    private var lastConnectedRef: DatabaseReference
    private var smartFarming: DatabaseReference? = null
    private var prefManager: SharedPreferences
    private var feedbackRef: DatabaseReference
    private var userDataRef: DatabaseReference
    private var storageUserRef: StorageReference

    private var artikelRef: CollectionReference
    private var penyakitRef: CollectionReference
    private var userFav: CollectionReference


    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

    private val _petani = MutableLiveData<Petani?>()
    val petani: MutableLiveData<Petani?> = _petani

    private val _allPetani = MutableLiveData<List<Petani>>()
    val allPetani: MutableLiveData<List<Petani>> = _allPetani

    private val _allKebun = MutableLiveData<List<Kebun>>()
    val allKebun: LiveData<List<Kebun>> = _allKebun

    private val _message = MutableLiveData<Pair<Boolean, Event<String>>>()
    val message: LiveData<Pair<Boolean, Event<String>>> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _kebunPetani = MutableLiveData<List<Kebun>>()
    val kebunPetani: LiveData<List<Kebun>> = _kebunPetani

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    private val _kebun = MutableLiveData<Kebun?>()
    val kebun: LiveData<Kebun?> = _kebun

    private val _monitoring = MutableLiveData<MonitoringKebun?>()
    val monitoring: LiveData<MonitoringKebun?> = _monitoring

    private val _controlling = MutableLiveData<List<Device>?>()
    val controlling: LiveData<List<Device>?> = _controlling

    private val _listArtikel = MutableLiveData<List<Artikel>>()
    val listArtikel: LiveData<List<Artikel>> = _listArtikel

    private val _listPenyakit = MutableLiveData<List<PenyakitTumbuhan>>()
    val listPenyakit: LiveData<List<PenyakitTumbuhan>> = _listPenyakit

    private val _listArtikelTerpopuler = MutableLiveData<List<Artikel>>()
    val listArtikelTerpopuler: LiveData<List<Artikel>> = _listArtikelTerpopuler

    private val _listPenyakitTerpopuler = MutableLiveData<List<PenyakitTumbuhan>>()
    val listPenyakitTerpopuler: LiveData<List<PenyakitTumbuhan>> = _listPenyakitTerpopuler

    private val _artikel = MutableLiveData<Artikel>()
    val artikel: LiveData<Artikel> = _artikel

    private val _penyakit = MutableLiveData<PenyakitTumbuhan>()
    val penyakit: LiveData<PenyakitTumbuhan> = _penyakit

    private val _WeatherForcast = MutableLiveData<WeatherForcast>()
    val weatherForcast: LiveData<WeatherForcast> = _WeatherForcast

    var idActiveKebun = ""
    var lastPetaniId = ""

    init {
        mAuth = FirebaseAuth.getInstance()
        _firebaseUser.value = FirebaseAuth.getInstance().currentUser
        connectionRef = mDb.reference.child("connections")
        infoConnected = mDb.getReference(".info/connected")
        lastConnectedRef = mDb.reference.child("lastConnected")
        connectionRef.keepSynced(true)
        smartFarming = mDb.reference.child("smart_farming")
        feedbackRef = mDb.reference.child("feedbacks")
        userDataRef = mDb.reference.child("user_data/")
        storageUserRef = FirebaseStorage.getInstance().reference.child("user_profile")

        artikelRef = mFb.collection("artikel")
        penyakitRef = mFb.collection("penyakit_tumbuhan")
        userFav = mFb.collection("user_fav")
        manageConnection()

        prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        lastPetaniId = prefManager.getString(SESI_PETANI_ID, "") ?: ""

    }

    fun getWeatherForcast(province: String, city: String) {
        _isLoading.value = true
        val api = ApiConfig.getApiService().getWeatherForcast(province.trim(), city.trim())
        api.enqueue(object : Callback<ResponseWeather> {
            override fun onResponse(
                call: Call<ResponseWeather>,
                response: Response<ResponseWeather>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful) {
                    if(responseBody!=null){
                        //Log.d("TAG","repo "+responseBody)
                       _WeatherForcast.value= DataMapper.responseWeatherToWeatherData(responseBody)
                    }
                    _message.value = Pair(
                        false,
                        Event(
                            "Berhasil mendapatkan data perkiraan cuaca"
                        )
                    )
                } else {
                    _message.value = Pair(
                        true,
                        Event(
                            "Gagal mendapatkan data perkiraan cuaca"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<ResponseWeather>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }

    fun favoriteArtikel(artike: Artikel) {
        val favorite: Boolean
        val list = artike.favorites ?: mutableListOf()
        val s: FieldValue = if (isFavorite(list)) {
            favorite = false
            FieldValue.arrayRemove(getCurrentUserUid())
        } else {
            favorite = true
            FieldValue.arrayUnion(getCurrentUserUid())
        }
        artikelRef.document(artike.id_artikel).update("favorites", s).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                artikelRef.document(artike.id_artikel)
                    .update("fav_count", FieldValue.increment(if (favorite) 1 else -1))
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            _message.value = Pair(
                                false,
                                Event(if (favorite) "Artikel disimpan di list disukai" else "Artikel dihapus dari list disukai")
                            )
                        } else {
                            Log.d("TAG", "error")
                        }
                    }

            } else {
                _message.value = Pair(
                    false,
                    Event(if (favorite) "Gagal menyimpan artikel" else "Gagal menghapus artikel")
                )
            }
        }
    }

    private fun isFavorite(list: MutableList<String>): Boolean {
        return list.contains(getCurrentUserUid())
    }

    fun favoritePenyakit(penyakit: PenyakitTumbuhan) {
        val favorite: Boolean
        val list = penyakit.favorites ?: mutableListOf()
        val s: FieldValue = if (isFavorite(list)) {
            favorite = false
            FieldValue.arrayRemove(getCurrentUserUid())
        } else {
            favorite = true
            FieldValue.arrayUnion(getCurrentUserUid())
        }
        penyakitRef.document(penyakit.id_penyakit).update("favorites", s)
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    penyakitRef.document(penyakit.id_penyakit)
                        .update("fav_count", FieldValue.increment(if (favorite) 1 else -1))
                        .addOnCompleteListener {
                            Log.d("TAG", "Penyakit fav")
                            if (it.isSuccessful) {
                                _message.value = Pair(
                                    false,
                                    Event(if (favorite) "Penyakit tumbuhan disimpan di list disukai" else "Penyakit tumbuhan dihapus dari list disukai")
                                )
                            } else {
                                Log.d("TAG", "error")
                            }
                        }

                } else {
                    _message.value = Pair(
                        false,
                        Event(if (favorite) "Gagal menyimpan penyakit tumbuhan" else "Gagal menghapus penyakit tumbuhan")
                    )
                }
            }
    }

    fun getArtikel(idArtikel: String) {
        artikelRef.whereEqualTo("id_artikel", idArtikel).get().addOnCompleteListener {
            if (it.isSuccessful) {
                try {
                    _artikel.value = it.result.toObjects(Artikel::class.java)[0]
                } catch (e: Exception) {
                    Log.d("TAG", e.message.toString())
                }

            } else {
                Log.d("TAG", context.getString(R.string.gagal_mendapat_data))
            }
        }
    }

    fun getPenyakit(idPenyakit: String) {
        penyakitRef.whereEqualTo("id_penyakit", idPenyakit).get().addOnCompleteListener {
            if (it.isSuccessful) {
                try {
                    _penyakit.value = it.result.toObjects(PenyakitTumbuhan::class.java)[0]
                } catch (e: Exception) {
                    Log.d("TAG", "Error")
                }
            } else {
                Log.d("TAG", context.getString(R.string.gagal_mendapat_data))
            }
        }
    }


    fun getArtikelTerpopuler() {
        val query = getQueryByFavCount(Kategori.ARTIKEL, 4)
        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = mutableListOf<Artikel>()
                try {
                    for (i in it.result) {
                        val x = i.toObject<Artikel>()
                        list.add(x)
                    }
                    Log.d("TAG", "yyyyyy")
                    _listArtikelTerpopuler.value = list
                } catch (e: Exception) {
                    _listArtikelTerpopuler.value = listOf()
                    Log.d("TAG", e.message.toString())
                }
                _isLoading.value = false
            } else {
                _listArtikelTerpopuler.value = listOf()
                _isLoading.value = false
                Log.d("TAG", "Error2")
            }
        }
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

    fun getListArtikelByKategori(kategori: KategoriArtikel) {
        _isLoading.value = true
        val query = getQueryByKategori(kategori, 8)
        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = mutableListOf<Artikel>()
                try {
                    for (i in it.result) {
                        val x = i.toObject<Artikel>()
                        list.add(x)
                    }
                    _listArtikel.value = list
                } catch (e: Exception) {
                    _listArtikel.value = listOf()
                    Log.d("TAG", e.message.toString())
                }
                _isLoading.value = false
            } else {
                _listArtikel.value = listOf()
                _isLoading.value = false
                Log.d("TAG", it.exception?.message.toString())
            }
        }
    }

    fun getPenyakitTerpopuler() {
        val query = getQueryByFavCount(Kategori.PENYAKIT, 4)
        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = mutableListOf<PenyakitTumbuhan>()
                try {
                    for (i in it.result) {
                        val x = i.toObject<PenyakitTumbuhan>()
                        list.add(x)
                    }
                    Log.d("TAG", "yyyyyy")
                    _listPenyakitTerpopuler.value = list
                } catch (e: Exception) {
                    _listPenyakitTerpopuler.value = listOf()
                    Log.d("TAG", e.message.toString())
                }
                _isLoading.value = false
            } else {
                _listPenyakitTerpopuler.value = listOf()
                _isLoading.value = false
                Log.d("TAG", "Error2")
            }
        }
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

    fun getListPenyakit() {
        _isLoading.value = true
        val query = getQueryByTime(Kategori.PENYAKIT, false, 8)
        query.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val list = mutableListOf<PenyakitTumbuhan>()
                try {
                    for (i in it.result) {
                        val x = i.toObject<PenyakitTumbuhan>()
                        list.add(x)
                    }
                    _listPenyakit.value = list
                } catch (e: Exception) {
                    _listPenyakit.value = listOf()
                    Log.d("TAG", e.message.toString())
                }
                _isLoading.value = false
            } else {
                _listPenyakit.value = listOf()
                _isLoading.value = false
                Log.d("TAG", "Error2")
            }
        }
    }

    private fun getQueryByFavCount(ref: Kategori, limit: Long): Query {
        val reff = if (ref == Kategori.PENYAKIT) penyakitRef else artikelRef
        return reff.orderBy("fav_count", DESCENDING)
            .limit(limit)
    }

    private fun getQueryByKategori(kategori: KategoriArtikel, limit: Long): Query {
        return if (kategori == KategoriArtikel.SEMUA) {
            artikelRef.orderBy("timestamp", DESCENDING)
                .limit(limit)
        } else {
            artikelRef.orderBy("timestamp", DESCENDING)
                .whereEqualTo("kategori", kategori.printable)
                .limit(limit)
        }
    }

    private fun getQueryPenyakitByKeyword(limit: Long, keyword: String = ""): Query {
        return penyakitRef
            .orderBy("title")
            .startAt(keyword)
            .endAt(keyword + "~")
            .limit(limit)
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
                .endAt(keyword + "~")
                .limit(limit)
        } else {
            artikelRef
                .whereEqualTo("kategori", kategori.printable)
                .orderBy("title")
                .startAt(keyword)
                .endAt(keyword + "~")
                .limit(limit)
        }
    }

    private fun getQueryByTime(ref: Kategori, onlyFav: Boolean, limit: Long): Query {
        val reff = if (ref == Kategori.PENYAKIT) penyakitRef else artikelRef
        val uid = getCurrentUserUid() ?: ""
        return if (onlyFav) {
            reff.orderBy("timestamp", DESCENDING)
                .whereArrayContains("favorites", uid)
                .limit(limit)
        } else {
            reff.orderBy("timestamp", DESCENDING)
                .limit(limit)
        }
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

    fun uploadProfilePicture(filePath: Uri) {
        _isLoading.value = true
        getCurrentUserUid()?.let { uid ->
            storageUserRef.child(uid).putFile(filePath).addOnCompleteListener {
                if (it.isSuccessful) {
                    storageUserRef.child(uid).downloadUrl.addOnCompleteListener { uri ->
                        if (uri.isSuccessful) {
                            getCurrentUserUid()?.let { it1 ->
                                userDataRef.child(it1).child("img_profile")
                                    .setValue(uri.result.toString()).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val profileUpdates = UserProfileChangeRequest.Builder()
                                                .setPhotoUri(uri.result)
                                                .build()
                                            FirebaseAuth.getInstance().currentUser?.updateProfile(
                                                profileUpdates
                                            )
                                            _isLoading.value = false
                                            _message.value =
                                                Pair(
                                                    false,
                                                    Event(context.getString(R.string.berhasil_ubah_data))
                                                )
                                        } else {
                                            _isLoading.value = false
                                            _message.value = Pair(
                                                true,
                                                Event(context.getString(R.string.gagal_ubah_data))
                                            )
                                        }
                                    }
                            }
                        } else {
                            _isLoading.value = false
                            _message.value =
                                Pair(true, Event(context.getString(R.string.gagal_ubah_data)))
                        }
                    }
                } else {
                    _isLoading.value = false
                    _message.value = Pair(true, Event(context.getString(R.string.gagal_ubah_data)))
                }
            }
        }
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

    fun setSelaluLoginPetani(isAlwaysLogin: Boolean) {
        if (isAlwaysLogin) {
            prefManager.edit().putBoolean(IS_ALWAYS_LOGIN_PETANI, true).apply()
        } else {
            logoutPetani()
            prefManager.edit().putBoolean(IS_ALWAYS_LOGIN_PETANI, false).apply()
        }
    }

    private fun manageConnection() {
        infoConnected.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)
                val userCon = getCurrentUserUid()?.let {
                    connectionRef.child(it)
                }
                val userLastCon = getCurrentUserUid()?.let {
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

    fun updateUserData(data: UserData) {
        _isLoading.value = true
        getCurrentUserUid()?.let { it ->
            userDataRef.child(it).setValue(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(data.name)
                        .build()
                    FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
                    _isLoading.value = false
                    _message.value =
                        Pair(false, Event(context.getString(R.string.berhasil_ubah_data)))
                } else {
                    _isLoading.value = false
                    _message.value = Pair(true, Event(context.getString(R.string.gagal_ubah_data)))
                }
            }
        }
    }

    fun getCurrentUserUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun kirimMasukan(data: Masukan) {
        val key = feedbackRef.push().key
        _isLoading.value = true
        key?.let { it ->
            feedbackRef.child(it).setValue(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    _isLoading.value = false
                    _message.value =
                        Pair(
                            false,
                            Event(context.resources.getString(R.string.berhasil_kirim_masukan))
                        )
                    updateCounterFeedback()
                } else {
                    _isLoading.value = false
                    _message.value =
                        Pair(true, Event(context.resources.getString(R.string.gagal_kirim_masukan)))
                }
            }
        }
    }

    fun getAllPetani(petaniName: String = "") {
        getCurrentUserUid()?.let { it ->
            smartFarming?.child(it)?.child("petani")
                ?.addValueEventListener(object : ValueEventListener {
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
                                    Log.d("TAG", e.message.toString())
                                }
                            }
                            _allPetani.value = list
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("TAG", error.message)
                    }
                })
        }
    }

    fun getAllKebun(kebunName: String = "") {
        getCurrentUserUid()?.let { it ->
            smartFarming?.child(it)?.child("kebun")
                ?.addValueEventListener(object : ValueEventListener {
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
                                    Log.d("TAG", e.message.toString())
                                }
                            }
                            _allKebun.value = list
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("TAG", error.message)
                    }

                })
        }
    }

    fun autoLoginPetani(idPetani: String) {
        val query = getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("petani")?.child(idPetani)
        }
        query?.keepSynced(true)

        query?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    try {
                        _petani.value = snapshot.getValue(Petani::class.java)
                    } catch (e: Exception) {
                        Log.d("TAG", e.message.toString())
                    }

                } else {
                    _message.value = Pair(
                        true,
                        Event(context.resources.getString(R.string.id_petani_not_found))
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", error.message)
            }
        })
    }

    fun clearDataKebun() {
        _kebun.value = null
        _controlling.value = null
        _monitoring.value = null
        idActiveKebun = ""
    }

    fun loginPetani(idPetani: String, passPetani: String) {

        val query = getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("petani")?.child(idPetani)
        }
        query?.keepSynced(true)


        if (_isConnected.value == false) {
            _isLoading.value = false
        } else if (_isConnected.value == true) {
            _isLoading.value = true
        }
        query?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    first@ for (i in snapshot.children) {
                        if (i.key.equals("password_petani")) {
                            if (i.value == passPetani) {
                                prefManager.edit().putString(SESI_PETANI_ID, idPetani).apply()
                                lastPetaniId = idPetani
                                //_idPetani.value = idPetani
                                try {
                                    _petani.value = snapshot.getValue(Petani::class.java)
                                } catch (e: Exception) {
                                    Log.d("TAG", e.message.toString())
                                }
                            } else {
                                _message.value = Pair(
                                    true,
                                    Event(context.resources.getString(R.string.wrong_password))
                                )
                            }
                            break@first
                        }
                    }

                } else {
                    Log.d("TAG", "id petani not found")
                    _message.value = Pair(
                        true,
                        Event(context.resources.getString(R.string.id_petani_not_found))
                    )
                }
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
                _message.value = Pair(
                    true,
                    Event(context.resources.getString(R.string.error))
                )
            }
        })

    }

    fun logoutPetani() {
        _kebunPetani.value = listOf()
        _kebun.value = null
        _monitoring.value = null

        /*getCurrentUserUid()?.let {
            _idPetani.value?.let { it1 -> smartFarming?.child(it)?.child("petani")?.child(it1) }
        }?.keepSynced(false)*/

        //_idPetani.value = null
        _petani.value = null
        getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("realtime_kebun")?.keepSynced(false)
        }
        prefManager.edit().putString(SESI_PETANI_ID, "").apply()
    }

    fun getAllKebunPetani(idPetani: String, kebunName: String = "") {
        val queryAll = getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("kebun")?.orderByChild("id_petani")?.equalTo(idPetani)
        }
        queryAll?.keepSynced(true)
        queryAll?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = arrayListOf<Kebun>()
                _kebunPetani.value = list
                if (snapshot.exists() && idPetani == lastPetaniId) {
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
                            Log.d("TAG", e.message.toString())
                        }
                    }
                    _kebunPetani.value = list
                } else {
                    //_kebunPetani.value = list
                    _message.value =
                        (Pair(false, Event(context.resources.getString(R.string.tidak_ada_kebun))))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //kebunPetani.value = list
                Log.d("TAG", error.message)
            }

        })

    }

    fun getKebun(idKebun: String) {
        _kebun.value = null
        getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("kebun")?.child(idKebun)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && snapshot.key == idActiveKebun) {
                            _kebun.value = snapshot.getValue(Kebun::class.java)
                        } else {
                            _kebun.value = null
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        _kebun.value = null
                        Log.d("TAG", error.message)
                    }
                })
        }
    }

    fun updateDeviceState(idKebun: String, idDevice: String, value: Int) {
        getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("realtime_kebun")?.child(idKebun)?.child("controlling")
                ?.child(idDevice)?.child("state")
                ?.setValue(value)
        }
    }

    fun getRealtimeKebun(idKebun: String) {
        getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("realtime_kebun")?.keepSynced(true)
        }

        getCurrentUserUid()?.let { it ->
            smartFarming?.child(it)?.child("realtime_kebun")?.child(idKebun)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val pompa = arrayListOf<Device>()
                        val lampu = arrayListOf<Device>()
                        val sprinkler = arrayListOf<Device>()
                        val drip = arrayListOf<Device>()
                        val fogger = arrayListOf<Device>()
                        val sirine = arrayListOf<Device>()
                        val cctv = arrayListOf<Device>()

                        val devices = arrayListOf<Device>()
                        if (snapshot.exists() && snapshot.key == idActiveKebun) {
                            for (i in snapshot.children) {
                                if (i.key.equals("monitoring")) {
                                    _monitoring.value = i.getValue(MonitoringKebun::class.java)
                                } else if (i.key.equals("controlling") && snapshot.key == idKebun) {
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
                                                //devices.add(it)
                                            }
                                        } catch (e: Exception) {
                                            Log.d("TAG", e.message.toString())
                                        }
                                    }
                                    _controlling.value =
                                        pompa.asSequence().plus(lampu).plus(sprinkler).plus(drip)
                                            .plus(fogger)
                                            .plus(sirine).plus(cctv).plus(devices)
                                            .toList()
                                }
                            }
                        } else {
                            _controlling.value = null
                            _monitoring.value = null

                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        _monitoring.value = null
                        Log.d("TAG", error.message)
                    }
                })
        }
    }

    fun login(email: String, pass: String) {
        _isLoading.value = true
        mAuth?.signInWithEmailAndPassword(email, pass)?.addOnCompleteListener {
            if (it.isSuccessful) {
                _isLoading.value = false
                //val name = FirebaseAuth.getInstance().currentUser?.displayName ?: ""
                _message.value =
                    Pair(false, Event(context.resources.getString(R.string.msg_login_successfully)))
                _firebaseUser.value = FirebaseAuth.getInstance().currentUser
            } else {
                _isLoading.value = false
                _message.value =
                    Pair(
                        true,
                        Event(context.resources.getString(R.string.msg_email_pass_might_be_wrong))
                    )
            }
        }

    }

    fun signOut() {
        if (_firebaseUser.value != null) {
            if (FirebaseAuth.getInstance().currentUser != null) {
                logoutPetani()

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.resources.getString(R.string.default_web_client_id_2))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInClient.signOut()

                _allKebun.value = listOf()
                _allPetani.value = listOf()
                _firebaseUser.value = null
                FirebaseAuth.getInstance().signOut()
            }
        }
    }

    fun registerAccount(email: String, pass: String, name: String, telepon: String) {
        _isLoading.value = true
        mAuth?.createUserWithEmailAndPassword(email, pass)?.addOnCompleteListener {

            if (it.isSuccessful) {
                _isLoading.value = false
                setUserData(email, name, telepon)
            } else {
                _isLoading.value = false
                _message.value = Pair(true, Event(it.exception?.message.toString()))
            }
        }


    }

    fun firebaseAuthWithGoogle(idToken: String) {
        _isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isLoading.value = false
                    _firebaseUser.value = FirebaseAuth.getInstance().currentUser
                    setUserData()
                } else {
                    _isLoading.value = false
                    _message.value =
                        Pair(
                            true,
                            Event(context.resources.getString(R.string.msg_failed_to_register))
                        )

                }
            }


    }

    private fun setUserData(email: String = "", name: String = "", telepon: String = "") {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        if (name != "") {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            currentUser?.updateProfile(profileUpdates)
        }

        val photo = if (currentUser?.photoUrl != null) currentUser.photoUrl.toString() else ""
        val user = UserData(
            currentUser?.email ?: email,
            currentUser?.displayName ?: name,
            telepon,
            currentUser?.uid ?: "",
            photo
        )


        currentUser?.uid?.let { it ->
            userDataRef.child(it).setValue(user).addOnCompleteListener { v ->
                if (v.isSuccessful) {
                    _firebaseUser.value = FirebaseAuth.getInstance().currentUser
                    _message.value = if (name != "") {
                        Pair(
                            false,
                            Event(context.resources.getString(R.string.msg_account_registered))
                        )
                    } else {
                        Pair(
                            false,
                            Event(context.resources.getString(R.string.msg_login_with_google_acc))
                        )
                    }
                    _isLoading.value = false
                } else {
                    _isLoading.value = false
                    _message.value = Pair(true, Event(v.exception?.message.toString()))
                    val errorUser: FirebaseUser = currentUser
                    signOut()
                    errorUser.delete()
                }
            }
        }

    }

    fun getUserData(): MutableLiveData<UserData?> {
        val userData = MutableLiveData<UserData?>()
        getCurrentUserUid()?.let {
            mDb.getReference("user_data").child(it)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            try {
                                val user = snapshot.getValue(UserData::class.java)
                                userData.value = user
                            } catch (e: Exception) {
                                Log.d("TAG", e.message.toString())

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("TAG", error.toString())
                    }

                })
        }

        return userData
    }

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(context: Application): Repository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    val mDb: FirebaseDatabase?
                    val mFb: FirebaseFirestore?
                    mDb =
                        FirebaseDatabase.getInstance(FIREBASE_URL)
                    mDb.setPersistenceEnabled(true)

                    mFb = FirebaseFirestore.getInstance()
                    instance = Repository(context, mDb, mFb)
                }
                return instance as Repository
            }

        }

        private const val FIREBASE_URL =
            "https://smart-farming-andro-default-rtdb.asia-southeast1.firebasedatabase.app/"
    }

}