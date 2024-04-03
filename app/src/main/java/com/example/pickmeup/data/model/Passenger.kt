package com.example.pickmeup.data.model

data class Passenger(
    val id: Int,
    val name: String,
    val surname: String,
    val password: String,
    val phone: String,
    val photo: String,
    val emergencyNumber: String
)
