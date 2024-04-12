package com.example.pickmeup.data.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SharedViewModel : ViewModel() {
    val pickUpTitle: MutableState<String> = mutableStateOf("")
    val targetTitle: MutableState<String> = mutableStateOf("")

    val pickUpLatLng: MutableState<LatLng> = mutableStateOf(LatLng(33.8938,35.5018))
    val targetLatLng: MutableState<LatLng> = mutableStateOf(LatLng(33.8938,35.5018))

    val distance:   MutableState<Double> = mutableStateOf(0.0)

    val dateAndTime: MutableState<String> = mutableStateOf("")
    fun setPickUpTitle(title: String) {
        pickUpTitle.value = title
    }

    fun setTargetTitle(title: String) {
        targetTitle.value = title
    }

    fun setPickUpLatLng(title: LatLng){
        pickUpLatLng.value = title
    }
    fun setTargetLatLng(title: LatLng){
        targetLatLng.value = title
    }
    fun setDistance(title: Double){
        distance.value= title
    }
    fun setDateAndTime(dateTime: String) {
        dateAndTime.value = dateTime
    }
}
