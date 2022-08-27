package com.example.taburtuai.data

import android.app.Application
import android.content.SharedPreferences
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taburtuai.R
import com.example.taburtuai.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import java.util.*

class Repository(private val context: Application, private var mDb: FirebaseDatabase) {

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

    // private val _idPetani = MutableLiveData<String?>()
    //val idPetani: LiveData<String?> = _idPetani

    private val _kebunPetani = MutableLiveData<List<Kebun>>()
    val kebunPetani: LiveData<List<Kebun>> = _kebunPetani

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    //private val _lastConnected = MutableLiveData<String>()
    //val lastConnected: LiveData<String> = _lastConnected

    private val _kebun = MutableLiveData<Kebun?>()
    val kebun: LiveData<Kebun?> = _kebun

    private val _monitoring = MutableLiveData<MonitoringKebun?>()
    val monitoring: LiveData<MonitoringKebun?> = _monitoring

    private val _controlling = MutableLiveData<List<Device>?>()
    val controlling: LiveData<List<Device>?> = _controlling

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
        userDataRef = mDb.reference.child("user_data")

        manageConnection()

        prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        //setSelaluLoginPetani(false)
        lastPetaniId = prefManager.getString(SESI_PETANI_ID, "") ?: ""

/*        prefManager.edit().putInt(FEEDBACK_COUNT,0).apply()
        prefManager.edit().putString(LAST_DATE_SEND_FEEDBACK,"").apply()*/
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
        _isLoading.value=true
        getCurrentUserUid()?.let { it ->
            userDataRef.child(it).setValue(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    _isLoading.value=false
                    _message.value=Pair(false, Event(context.getString(R.string.berhasil_ubah_data)))
                } else {
                    _isLoading.value=false
                    _message.value=Pair(true,Event(context.getString(R.string.gagal_ubah_data)))
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
                    //_idPetani.value = idPetani
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
                                        pompa.asSequence().plus(lampu).plus(sprinkler).plus(drip).plus(fogger)
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
                    //_message.value = Pair(false, context.resources.getString(R.string.msg_login_with_google_acc))
                    //_firebaseUser.value = FirebaseAuth.getInstance().currentUser
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
                    mDb =
                        FirebaseDatabase.getInstance(FIREBASE_URL)
                    mDb.setPersistenceEnabled(true)
                    instance = Repository(context, mDb)
                }
                return instance as Repository
            }

        }

        private const val FIREBASE_URL =
            "https://smart-farming-andro-default-rtdb.asia-southeast1.firebasedatabase.app/"
    }

}