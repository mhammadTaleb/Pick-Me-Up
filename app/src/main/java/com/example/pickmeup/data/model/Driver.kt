package com.example.pickmeup.data.model

data class Driver(
    val id: Int,
    val name: String,
    val surname: String,
    val password: String,
    val phone: String,
    val license: String,
    val carPlate: String,
    val carPhoto: String,
    val driverPhoto: String,
    val driverLicense: String,
    val driverStatus: String,
    val driverActive: Boolean
)
