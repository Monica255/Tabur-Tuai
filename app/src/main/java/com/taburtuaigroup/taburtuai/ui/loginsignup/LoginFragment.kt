package com.taburtuaigroup.taburtuai.ui.loginsignup

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.databinding.FragmentLoginBinding
import com.taburtuaigroup.taburtuai.ui.home.HomeActivity
import com.taburtuaigroup.taburtuai.core.util.Event
import com.taburtuaigroup.taburtuai.core.util.LoadingUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.taburtuaigroup.taburtuai.core.data.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginSignupViewModel by viewModels()
    private var email = ""
    private var pass = ""
    private lateinit var googleSignInClient: GoogleSignInClient
    private var errorMsg: Event<Any>? = null
    private var isEmailValid = false
        get() {
            checkEmail()
            return field
        }
    private var isPasswordValid = false
        get() {
            checkPassword()
            return field
        }

    private val resultCOntract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result?.data)
                val exception = task.exception
                if (task.isSuccessful) {
                    val account = task.getResult(ApiException::class.java)
                    lifecycleScope.launch {
                        viewModel.firebaseAuthWithGoogle(account.idToken.toString())
                            .observe(viewLifecycleOwner) {
                                when (it) {
                                    is Resource.Loading -> {
                                        showLoading(true)
                                    }
                                    is Resource.Success -> {
                                        showLoading(false)
                                        goHome()
                                    }
                                    is Resource.Error -> {
                                        showLoading(false)
                                        it.data?.let { it1 ->
                                            errorMsg = Event(it1)
                                            showToast()
                                        }
                                    }
                                }
                            }
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_2))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        setAction()
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            LoadingUtils.showLoading(activity, false)
        } else {
            LoadingUtils.hideLoading()
        }
    }

    private fun setAction() {
        binding.btSudahPunyaAkun.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.btMasuk.setOnClickListener {
            if (isDataValid()) {
                lifecycleScope.launch {
                    viewModel.login(email, pass).observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Loading -> {
                                Log.d("TAG", "loading")
                                showLoading(true)
                            }
                            is Resource.Success -> {
                                Log.d("TAG", "success")
                                showLoading(false)
                                goHome()
                            }
                            is Resource.Error -> {
                                it.data?.let { it1 ->
                                    Log.d("TAG", it1)
                                    errorMsg = Event(it1)
                                    showToast()
                                }
                            }
                        }
                    }
                }
            } else if (!isFieldsEmpty()) {
                showToast()
            }
        }

        binding.cbShowPass.setOnClickListener {
            if (binding.cbShowPass.isChecked) {
                binding.etMasukPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.etMasukPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }

        binding.btGoogle.setOnClickListener {
            signIn()
        }

    }

    private fun goHome(){
        startActivity(
            Intent(
                activity,
                HomeActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun isFieldsEmpty(): Boolean {
        return binding.etMasukEmail.text.toString().trim() == ""
                && binding.etMasukPassword.text.toString().trim() == ""

    }

    private fun isDataValid(): Boolean {
        errorMsg = null
        return isEmailValid && isPasswordValid
    }

    private fun showToast() {
        val msg = errorMsg?.getContentIfNotHandled() ?: return
        Toast.makeText(
            requireContext(),
            when (msg) {
                //is Int -> getString(msg)
                is String -> msg
                else -> "Error"
            }, Toast.LENGTH_SHORT
        ).show()

    }

    private fun checkEmail() {
        email = binding.etMasukEmail.text.toString().trim()
        if (email.isEmpty()) {
            isEmailValid = false
            errorMsg = Event(getString(R.string.email_empty))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isEmailValid = false
            errorMsg = Event(getString(R.string.email_invalid))
        } else {
            isEmailValid = true
        }
    }

    private fun checkPassword() {
        pass = binding.etMasukPassword.text.toString().trim()
        if (pass.isEmpty()) {
            isPasswordValid = false
            errorMsg = Event(getString(R.string.password_empty))
        } else if (pass.length < 6) {
            isPasswordValid = false
            errorMsg = Event(getString(R.string.password_length_invalid))
        } else {
            isPasswordValid = true
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultCOntract.launch(signInIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}