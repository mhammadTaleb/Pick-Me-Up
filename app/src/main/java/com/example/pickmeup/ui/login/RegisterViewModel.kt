package com.example.pickmeup.ui.login

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    var firstName = mutableStateOf("")
        private set

    var lastName = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var confirmPassword = mutableStateOf("")
        private set

    var role = mutableIntStateOf(0)
        private set

    fun updateFirstName(newFirstName: String) {
        firstName.value = newFirstName
    }

    fun updateLastName(newLastName: String) {
        lastName.value = newLastName
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    fun updateRole(newRole: Int) {
        if (role.intValue != newRole)
            role.intValue = newRole
    }

    fun passwordsMatch(): Boolean {
        return password.value == confirmPassword.value
    }

    fun inputsFilled(): Boolean {
        return firstName.value.isNotEmpty() && lastName.value.isNotEmpty() && password.value.isNotEmpty() && confirmPassword.value.isNotEmpty() && passwordsMatch()
    }
}