package com.example.pickmeup.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var regPassword by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")

    fun checkPasswordStrength() {
        TODO("Not yet implemented")
    }

}