package com.example.taburtuai.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taburtuai.R
import com.example.taburtuai.util.Event
import com.example.taburtuai.util.IS_ALWAYS_LOGIN_PETANI
import com.example.taburtuai.util.LAST_UPDATE
import com.example.taburtuai.util.SESI_PETANI_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Repository(private val context: Context, private var mDb: FirebaseDatabase) {

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    private var mAuth: FirebaseAuth? = null
    //private var mDb: FirebaseDatabase? = null

    private var connectionRef: DatabaseReference
    private var infoConnected: DatabaseReference
    private var lastConnectedRef: DatabaseReference
    private var smartFarming: DatabaseReference? = null
    private var prefManager: SharedPreferences


    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

    private val _petani = MutableLiveData<Petani?>()
    val petani: MutableLiveData<Petani?> = _petani

    private val _message = MutableLiveData<Pair<Boolean, Event<String>>>()
    val message: LiveData<Pair<Boolean, Event<String>>> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _idPetani = MutableLiveData<String?>()
    val idPetani: LiveData<String?> = _idPetani

    private val _kebunPetani = MutableLiveData<List<Kebun>>()
    val kebunPetani: LiveData<List<Kebun>> = _kebunPetani

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    private val _lastConnected = MutableLiveData<String>()
    val lastConnected: LiveData<String> = _lastConnected

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

        manageConnection()

        prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        setPetaniLoginState(false)
        lastPetaniId = prefManager.getString(SESI_PETANI_ID, "") ?: ""


    }

    fun setPetaniLoginState(isAlwaysLogin: Boolean) {
        if (isAlwaysLogin) {
            prefManager.edit().putBoolean(IS_ALWAYS_LOGIN_PETANI, true).apply()
        } else {
            prefManager.edit().putBoolean(IS_ALWAYS_LOGIN_PETANI, false).apply()
            prefManager.edit().putString(SESI_PETANI_ID, "").apply()
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
                    Log.d("TAG", connected.toString())
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


    fun getCurrentUserUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun autoLoginPetani(idPetani: String) {
        getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("petani")
        }?.keepSynced(true)

        smartFarming?.child(idPetani)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _idPetani.value = idPetani
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

    fun clearDataKebun(){
        _kebun.value=null
        _controlling.value=null
        _monitoring.value=null
        idActiveKebun=""
    }



    fun loginPetani(idPetani: String, passPetani: String) {
        getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("petani")
        }?.keepSynced(true)

        lastPetaniId = idPetani
        if (_isConnected.value == false) {
            _isLoading.value = false
        } else if (_isConnected.value == true) {
            _isLoading.value = true
        }
        getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("petani")
                ?.child(idPetani)?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("TAG", "login2 petani")
                        if (snapshot.exists()) {
                            first@ for (i in snapshot.children) {
                                if (i.key.equals("password_petani")) {
                                    if (i.value == passPetani) {
                                        Log.d("TAG", "login petani")
                                        _idPetani.value = idPetani
                                    } else {
                                        Log.d("TAG", "wrong pass")
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
                            Event(context.resources.getString(R.string.error)))
                    }
                })
        }
    }

    fun logoutPetani() {
        _idPetani.value = null
        _kebunPetani.value = listOf()
        _kebun.value = null
        _monitoring.value = null
        getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("petani")
        }?.keepSynced(false)
        getCurrentUserUid()?.let {
            smartFarming?.child(it)?.child("realtime_kebun")?.keepSynced(false)
        }
        prefManager.edit().putString(SESI_PETANI_ID, "").apply()
    }

    fun getAllKebun(idPetani: String, kebunName: String = "") {
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

                                list.add(it)
                            }
                        } catch (e: Exception) {
                            Log.d("TAG", e.message.toString())
                        }
                    }
                    if (kebunName != "") {
                        val newList = ArrayList<Kebun>()
                        for (i in list) {
                            if (i.nama_kebun.contains(kebunName, true)) {
                                newList.add(i)
                            }
                        }
                        _kebunPetani.value = newList
                    } else {
                        _kebunPetani.value = list
                    }
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
                        val devices = arrayListOf<Device>()
                        if (snapshot.exists() && snapshot.key == idActiveKebun) {
                            for (i in snapshot.children) {
                                if (i.key.equals("monitoring")) {
                                    _monitoring.value = i.getValue(MonitoringKebun::class.java)
                                } else if (i.key.equals("controlling") && snapshot.key == idKebun) {
                                    for (j in i.children) {
                                        try {
                                            j.getValue(Device::class.java)?.let { it ->
                                                if (it.id_device != "" && j.key.toString() == it.id_device) devices.add(
                                                    it
                                                )
                                            }
                                        } catch (e: Exception) {
                                            Log.d("TAG", e.message.toString())
                                        }
                                    }
                                    _controlling.value = devices
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
                val name = FirebaseAuth.getInstance().currentUser?.displayName ?: ""
                _message.value =
                    Pair(false, Event(context.resources.getString(R.string.msg_login_successfully)))
                _firebaseUser.value = FirebaseAuth.getInstance().currentUser
            } else {
                _isLoading.value = false
                _message.value =
                    Pair(true, Event(context.resources.getString(R.string.msg_email_pass_might_be_wrong)))
            }
        }

    }

    fun signOut() {
        if (_firebaseUser.value != null) {
            if (FirebaseAuth.getInstance().currentUser != null) {
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
                    //_message.value = Pair(false, context.resources.getString(R.string.msg_login_with_google_acc))
                    //_firebaseUser.value = FirebaseAuth.getInstance().currentUser
                    setUserData()
                } else {
                    _isLoading.value = false
                    _message.value =
                        Pair(true, Event(context.resources.getString(R.string.msg_failed_to_register)))

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
            mDb.getReference("user_data").child(it).setValue(user).addOnCompleteListener { v ->
                if (v.isSuccessful) {
                    _firebaseUser.value = FirebaseAuth.getInstance().currentUser
                    _message.value = if (name != "") {
                        Pair(false, Event(context.resources.getString(R.string.msg_account_registered)))
                    } else {
                        Pair(false, Event(context.resources.getString(R.string.msg_login_with_google_acc)))
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
            mDb.getReference("user_data").orderByChild("uid").equalTo(it)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (i in snapshot.children) {
                                val user = i.getValue(UserData::class.java)
                                userData.value = user
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

        fun getInstance(context: Context): Repository {
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

        const val FIREBASE_URL =
            "https://smart-farming-andro-default-rtdb.asia-southeast1.firebasedatabase.app/"
    }

}