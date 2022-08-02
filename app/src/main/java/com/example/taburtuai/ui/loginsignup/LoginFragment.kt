package com.example.taburtuai.ui.loginsignup

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.taburtuai.R
import com.example.taburtuai.ViewModelFactory
import com.example.taburtuai.databinding.FragmentLoginBinding
import com.example.taburtuai.ui.home.HomeActivity
import com.example.taburtuai.util.Event
import com.example.taburtuai.util.LoadingUtils
import com.example.taburtuai.util.ToastUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginSignupViewModel
    private var email=""
    private var pass=""
    private lateinit var googleSignInClient: GoogleSignInClient
    private var errorMsg: Event<Any>?=null
    private var isEmailValid=false
    get() {
        checkEmail()
        return field
    }
    private var isPasswordValid=false
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
                    try {
                        val account = task.getResult(ApiException::class.java)
                        viewModel.firebaseAuthWithGoogle(account.idToken.toString())
                    } catch (e: ApiException) {
                        errorMsg=Event(e.toString())
                        showToast()
                    }
                } else {
                    errorMsg=Event(exception?.message.toString())
                    showToast()
                }
            } catch (e: Exception) {
                errorMsg=Event(e.toString())
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

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireActivity().application)
        )[LoginSignupViewModel::class.java]

        var goHome=true
        viewModel.firebaseUser.observe(requireActivity()){

            if(it!=null&&isAdded&&goHome){
                goHome=false
                startActivity(
                    Intent(
                        activity,
                        HomeActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }

        viewModel.message.observe(requireActivity()){
            if(isAdded) ToastUtil.makeToast(requireActivity(),it)
        }

        viewModel.isLoading.observe(requireActivity(),Observer(this::showLoading))

    }


    private fun showLoading(isLoading:Boolean){
        if(isLoading){
            LoadingUtils.showLoading(activity,false)
        }else{
            LoadingUtils.hideLoading()
        }
    }

    private fun setAction(){
        binding.btSudahPunyaAkun.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.btMasuk.setOnClickListener {
            if(isDataValid()){
                viewModel.login(email,pass)
            }else if(!isFieldsEmpty()){
                showToast()
            }
        }

        binding.cbShowPass.setOnClickListener {
            if (binding.cbShowPass.isChecked) {
                binding.etMasukPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.etMasukPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.btGoogle.setOnClickListener {
            signIn()
        }

    }
    private fun isFieldsEmpty(): Boolean {
        return binding.etMasukEmail.text.toString().trim() == ""
                && binding.etMasukPassword.text.toString().trim() == ""

    }

    private fun isDataValid():Boolean{
        errorMsg=null
        return isEmailValid&&isPasswordValid
    }

    private fun showToast(){
        val msg=errorMsg?.getContentIfNotHandled()?:return
        Toast.makeText(requireContext(),
            when(msg){
                is Int->getString(msg)
                is String->msg
                else->"Error"
            }
            ,Toast.LENGTH_SHORT).show()

    }

    private fun checkEmail(){
        email = binding.etMasukEmail.text.toString().trim()
        if (email.isEmpty()) {
            isEmailValid = false
            errorMsg=Event(R.string.email_empty)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isEmailValid = false
            errorMsg=Event(R.string.email_invalid)
        } else {
            isEmailValid = true
        }
    }

    private fun checkPassword(){
        pass = binding.etMasukPassword.text.toString().trim()
        if (pass.isEmpty()) {
            isPasswordValid = false
            errorMsg=Event(R.string.password_empty)
        } else if (pass.length < 6) {
            isPasswordValid = false
            errorMsg=Event(R.string.password_length_invalid)
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
        _binding=null
    }

}