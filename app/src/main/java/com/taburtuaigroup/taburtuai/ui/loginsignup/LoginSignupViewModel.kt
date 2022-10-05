package com.taburtuaigroup.taburtuai.ui.loginsignup

import androidx.lifecycle.ViewModel
import com.taburtuaigroup.taburtuai.data.Repository

class LoginSignupViewModel(private val repository: Repository) :ViewModel() {

    var firebaseUser=repository.firebaseUser

    var message=repository.message

    val isLoading=repository.isLoading

    fun login(email:String,pass:String)=repository.login(email,pass)

    fun register(email:String,pass: String,name:String,telepon:String)=repository.registerAccount(email,pass,name,telepon)

    fun firebaseAuthWithGoogle(idToken:String)=repository.firebaseAuthWithGoogle(idToken)
}