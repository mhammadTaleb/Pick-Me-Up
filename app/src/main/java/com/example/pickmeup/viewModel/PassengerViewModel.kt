package com.example.pickmeup.viewModel

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import com.example.pickmeup.data.model.SharedViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class PassengerViewModel {


    fun calculateDistance(origin: LatLng, destination: LatLng): Double {
        val earthRadius = 6371 // Radius of the earth in kilometers
        // Convert latitude and longitude to radians
        val latOrigin = Math.toRadians(origin.latitude)
        val lonOrigin = Math.toRadians(origin.longitude)
        val latDestination = Math.toRadians(destination.latitude)
        val lonDestination = Math.toRadians(destination.longitude)

        // Calculate the differences between the coordinates
        val dLat = latDestination - latOrigin
        val dLon = lonDestination - lonOrigin

        // Apply the Haversine formula
        val a = sin(dLat / 2).pow(2) + cos(latOrigin) * cos(latDestination) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distance = earthRadius * c

        // Format the distance to two decimal places
        return String.format("%.2f", distance).toDouble()}




    fun reverseGeocode(latLng: LatLng, context: Context): String? {
        val latitude = latLng.latitude
        val longitude = latLng.longitude

        return try {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address>? = geoCoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressLine = address.getAddressLine(0) ?: ""
                val streetName = address.thoroughfare ?: ""
                val fullAddress = if (streetName.isNotEmpty()) "$streetName, $addressLine" else addressLine

                // Split the full address by commas
                val parts = fullAddress.split(",")

                // Check each part for lowercase letters
                val cleanedParts = parts.map { part ->
                    val containsLowercase = part.any { it.isLowerCase() }
                    if (!containsLowercase) {
                        // Remove the part if it doesn't contain any lowercase letters
                        null
                    } else {
                        part.trim()
                    }
                }.filterNotNull()

                cleanedParts.joinToString(", ") // Join the cleaned parts back with commas
            } else {
                null
            }
        } catch (e: Exception) {

            Toast.makeText(context,"Can't get geolocation", Toast.LENGTH_SHORT).show()
            null
        }
    }



    fun getCurrentLocation(
        context: Context,
        onLocationReceived: (Double, Double) -> Unit,
        function: () -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        } else {
            // Permissions already granted, get the location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Use the location object as needed
                    //  Log.i("xxxx", "Location: ${location?.latitude}, ${location?.longitude}")
                    location?.let {
                        onLocationReceived(it.latitude, it.longitude)
                    }?: WhenItIsNull(context)
                }
        }
    }


    fun WhenItIsNull(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.apply {
            setTitle("Location Information")
            setMessage("Unable to retrieve your location. Please ensure that your GPS is turned on and that you have a good signal reception.")
            setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }


    fun ShowWifiProblemDialog(context: Context){
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.apply {
            setTitle("Wifi Information")
            setMessage("Unable to connect to the internet. Please ensure that your WiFi is turned on and that you have a good signal reception.")
            setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }



    @Composable
    fun InfoDialog(sharedViewModel: SharedViewModel, context: Context) {

        val text= "Pickup Title: ${sharedViewModel.pickUpTitle.value} \n" +
                "Target Title: ${sharedViewModel.targetTitle.value} \n" +
                "Pickup Latitude: ${sharedViewModel.pickUpLatLng.value.latitude}\n" +
                "Pickup Longitude: ${sharedViewModel.pickUpLatLng.value.longitude}\n" +
                "Target Latitude: ${sharedViewModel.targetLatLng.value.latitude}\n" +
                "Target Longitude: ${sharedViewModel.targetLatLng.value.longitude}\n" +
                "Distance: ${sharedViewModel.distance.value} Km\n"+
                "Date and Time: ${sharedViewModel.dateAndTime.value}"

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.apply {
            setTitle("Information")
            setMessage(text)
            setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

}