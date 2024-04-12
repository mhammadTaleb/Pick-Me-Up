package com.example.pickmeup.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var phoneNumber by mutableStateOf("")
        private set
    var password by mutableStateOf("")
    var role by mutableIntStateOf(0)

    fun inputsFilled(): Boolean {
        return phoneNumber.isNotEmpty() && password.isNotEmpty()
    }

    fun login() {
        if(role == 0) {
            // login as passenger
        } else {
            // login as driver
        }
    }

    fun updatePhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun updatePassword(password: String) {
        this.password = password
    }

    fun updateRole(role: Int) {
        this.role = role
    }

}