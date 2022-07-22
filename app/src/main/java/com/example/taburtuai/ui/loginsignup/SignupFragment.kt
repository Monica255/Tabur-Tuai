package com.example.taburtuai.ui.loginsignup

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.taburtuai.R
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.databinding.FragmentSignupBinding
import com.example.taburtuai.ui.home.HomeActivity
import com.example.taburtuai.util.Event
import com.example.taburtuai.util.LoadingUtils
import com.example.taburtuai.util.ToastUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


class SignupFragment : Fragment() {
    private lateinit var viewModel: LoginSignupViewModel
    private lateinit var binding: FragmentSignupBinding
    private var nama = ""
    private var email = ""
    private var telepon = ""
    private var password = ""
    private var cpassword = ""

    private lateinit var googleSignInClient: GoogleSignInClient

    private var errorMsg: Event<Any>? = null

    private var isNamaValid = false
        get() {
            checkName()
            return field
        }

    private var isEmailValid = false
        get() {
            checkEmail()
            return field
        }
    private var isTeleponValid = false
        get() {
            checkTelepon()
            return field
        }
    private var isPasswordValid = false
        get() {
            checkPassword()
            return field
        }
    private var isCPassValid = false


    private val resultCOntract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result?.data)
                val exception = task.exception
                if (task.isSuccessful) {
                    try {
                        val account = task.getResult(ApiException::class.java)
                        viewModel.firebaseAuthWithGoogle(account.idToken.toString())
                    } catch (e: ApiException) {
                        errorMsg = Event(e.toString())
                        showToast()
                    }
                } else {
                    errorMsg = Event(exception?.message.toString())
                    showToast()
                }
            } catch (e: Exception) {
                errorMsg = Event(e.toString())
                showToast()
            }
        }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultCOntract.launch(signInIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireActivity())
        )[LoginSignupViewModel::class.java]

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_2))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        setAction()

        viewModel.firebaseUser.observe(requireActivity()) {
            if (it != null) {
                startActivity(
                    Intent(
                        activity,
                        HomeActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else {

            }
        }

        binding.etDaftarPassword.addTextChangedListener {
            password = binding.etDaftarPassword.text.toString().trim()
            if (password == cpassword) {
                isCPassValid = true
                binding.ilDaftarCpassword.isErrorEnabled = false
                binding.ilDaftarCpassword.error = ""
            } else {
                isCPassValid = false
            }

        }

        binding.etDaftarCpassword.addTextChangedListener {
            cpassword = binding.etDaftarCpassword.text.toString().trim()
            if (password == cpassword) {
                isCPassValid = true
                binding.ilDaftarCpassword.isErrorEnabled = false
                binding.ilDaftarCpassword.error = ""
            } else {
                isCPassValid = false
            }
        }
        viewModel.message.observe(requireActivity()) {
            if(isAdded)ToastUtil.makeToast(requireActivity(),it)
        }

        viewModel.isLoading.observe(requireActivity(), Observer(this::showLoading))

    }

    private fun setAction() {
        binding.btSudahPunyaAkun.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_signupFragment_to_loginFragment)
        }

        binding.btDaftar.setOnClickListener {
            if (isDataValid()) {
                viewModel.register(name = nama, email = email, telepon = telepon, pass = password)
            } else if (!isFieldsEmpty()) {

                if (!isCPassValid) {
                    binding.ilDaftarCpassword.error = getString(R.string.password_not_match)
                }
                showToast()
            }
        }

        binding.cbShowPass.setOnClickListener {
            if (binding.cbShowPass.isChecked) {
                binding.etDaftarPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.etDaftarCpassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.etDaftarPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.etDaftarCpassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }
        binding.btGoogle.setOnClickListener {
            signIn()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            LoadingUtils.showLoading(activity, false)
        } else {
            LoadingUtils.hideLoading()
        }
    }

    private fun showToast() {
        val msg = errorMsg?.getContentIfNotHandled() ?: return
        Toast.makeText(
            requireContext(),
            when (msg) {
                is Int -> getString(msg)
                is String -> msg
                else -> "Error"
            }, Toast.LENGTH_SHORT
        ).show()

    }

    private fun isFieldsEmpty(): Boolean {
        return binding.etDaftarEmail.text.toString().trim() == ""
                && binding.etDaftarPassword.text.toString().trim() == ""
                && binding.etDaftarNama.text.toString().trim() == ""
                && binding.etDaftarTelepon.text.toString().trim() == ""
                && binding.etDaftarCpassword.text.toString().trim() == ""

    }

    private fun isDataValid(): Boolean {
        errorMsg = null
        return isEmailValid && isNamaValid && isTeleponValid && isPasswordValid && isCPassValid
    }

    private fun checkTelepon() {
        telepon = binding.etDaftarTelepon.text.toString().trim()
        if (telepon.isEmpty()) {
            isTeleponValid = false
            errorMsg = Event(R.string.phone_empty)
        } else if (telepon.length < 9) {
            isTeleponValid = false
            errorMsg = Event(R.string.phone_invalid)
        } else {
            isTeleponValid = true
        }
    }

    private fun checkName() {
        nama = binding.etDaftarNama.text.toString().trim()
        if (nama.isEmpty()) {
            isNamaValid = false
            errorMsg = Event(R.string.name_empty)
        } else {
            isNamaValid = true
        }
    }

    private fun checkEmail() {
        email = binding.etDaftarEmail.text.toString().trim()
        if (email.isEmpty()) {
            isEmailValid = false
            errorMsg = Event(R.string.email_empty)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isEmailValid = false
            errorMsg = Event(R.string.email_invalid)
        } else {
            isEmailValid = true
        }
    }

    private fun checkPassword() {
        password = binding.etDaftarPassword.text.toString().trim()
        if (password.isEmpty()) {
            isPasswordValid = false
            errorMsg = Event(R.string.password_empty)
        } else if (password.length < 6) {
            isPasswordValid = false
            errorMsg = Event(R.string.password_length_invalid)
        } else {
            isPasswordValid = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }


}