package com.example.pickmeup.ui.login

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider

class OTPViewModel: ViewModel(){
    var otp = mutableStateOf("")
        private set
    var phoneNumber = mutableStateOf("")
        private set

    private lateinit var auth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    init {
        auth = FirebaseAuth.getInstance()
        setupCallbacks()
    }
    fun updatePhoneNumber(newPhoneNumber: String){
        phoneNumber.value = newPhoneNumber
    }

    fun updateOTP(newOTP: String){
        otp.value = newOTP
    }
    private fun setupCallbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    // ...
                } else {
                    // Sign in failed, display a message and update the UI
                    // ...
                }
            }

    }

    fun authenticate(activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber.value)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOTP() {
        val credential = PhoneAuthProvider.getCredential(otp.value, "")
        signInWithPhoneAuthCredential(credential)
    }

fun register(registerViewModel: RegisterViewModel) {
    // Check the user's role
    if (registerViewModel.role.value == 0) { // Assuming 0 is for passenger
        // Create a new Passenger object
//        val passenger = Passenger(user.uid, registerViewModel.firstName.value, registerViewModel.lastName.value, phoneNumber.value)
        // Add the passenger to your database
//        DatabaseRepository().addPassenger(passenger)
    }
}}