package com.example.pickmeup.ui.passenger

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pickmeup.data.model.SharedViewModel
import com.example.pickmeup.ui.passenger.ui.theme.PickMeUpTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
class PassengerView : ComponentActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PickMeUpTheme {
                val items = listOf(
                    BottomNavigationItem("search", Icons.Filled.Search, Icons.Outlined.Search),
                    BottomNavigationItem("home", Icons.Filled.Home, Icons.Outlined.Home),
                    BottomNavigationItem("profile", Icons.Filled.Person, Icons.Outlined.Person)
                )
                var selectedItemIndex by remember { mutableStateOf(items.indexOfFirst { it.title == "home" }) }
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = (selectedItemIndex == index),
                                        onClick = {
                                            selectedItemIndex = index
                                            navController.navigate(item.title)
                                        },
                                        label = { Text(item.title) },
                                        alwaysShowLabel = false,
                                        icon = {
                                            Box {
                                                Icon(
                                                    imageVector = if (index == selectedItemIndex) {
                                                        item.selectedIcon
                                                    } else item.unselectedIcon,
                                                    contentDescription = item.title
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavHost(navController = navController, startDestination = "home") {
                                composable("search") {
                                    SearchScreen(navController)
                                }
                                composable("home") {
                                    HomeScreen(navController, this@PassengerView, sharedViewModel)
                                }
                                composable("profile") {
                                    ProfileScreen(navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun HomeScreen(navController: NavHostController, context: Context, sharedViewModel: SharedViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "pickUps") {
        composable("pickUps") {
            PickUps(context, navController, sharedViewModel)
        }
        composable("mapView") {
            MapView(context, navController, sharedViewModel)
        }
    }
}



@Composable
fun PickUps(context: Context, navController: NavHostController, sharedViewModel: SharedViewModel){


    var pickUpTitle by remember {
        mutableStateOf("Pick Up")
    }

    var targetTitle by remember {
        mutableStateOf("Destination")
    }

    var pickedDate by remember {
        mutableStateOf(LocalDate.now())
    }
    var pickedTime by remember{
        mutableStateOf(LocalTime.now())
    }
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("MMM dd yyyy")
                .format(pickedDate)
        }
    }
    val formattedTime by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("hh:mm")
                .format(pickedTime)
        }
    }

    val dateDialogState= rememberMaterialDialogState()
    val timeDialogState= rememberMaterialDialogState()

    var isButtonEnabled1 by remember { mutableStateOf(false) }
    var isButtonEnabled2 by remember { mutableStateOf(false) }

    val pickUpTitleTest = sharedViewModel.pickUpTitle.value
    val targetTitleTest = sharedViewModel.targetTitle.value

    val showDialog = remember { mutableStateOf(false) }


    if(pickUpTitleTest.isNotEmpty()){
        pickUpTitle= pickUpTitleTest
        isButtonEnabled1= true
    }
    if(targetTitleTest.isNotEmpty()){
        targetTitle= targetTitleTest
    }



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Request new Pick UP",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(start = 10.dp, top = 16.dp)
            )

        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 12.dp),
            horizontalArrangement = Arrangement.Start
        ){
            Text(
                text = "Location:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 135.dp, max = 180.dp), // row of two field and location button
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.weight(0.8f)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(                        // pick up location box
                        modifier = Modifier
                            .padding(start = 4.dp, end = 12.dp, top = 4.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        // row for pick up location and cancel button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pickUpTitle ,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                    Box(                    //target location box
                        modifier = Modifier
                            .padding(start = 4.dp, end = 12.dp, top = 12.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        // row for pick up location and cancel button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = targetTitle,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
            Box(                    //add button box
                modifier = Modifier
                    .weight(0.2f)
                    .padding(5.dp)
                    .heightIn(max = 135.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxSize(),          // Fill the entire available space in the box
                    onClick = {
                        navController.navigate("mapView")

                    },
                    shape = MaterialTheme.shapes.medium, // Set the button shape to medium (cubic)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Clock icon",
                            modifier = Modifier.padding(end = 8.dp) // Add padding to the right of the icon
                        )
                    }
                }
            }
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            horizontalArrangement = Arrangement.Start
        ){
            Text(
                text = "Schedule:",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

                // time row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.weight(0.8f)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(                        // pick up location box
                        modifier = Modifier
                            .padding(start = 4.dp, end = 12.dp, top = 15.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        // row for pick up location and cancel button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val dateAndTimeField= "$formattedDate, $formattedTime"
                            Text(
                                text = dateAndTimeField,                  // date & time text
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
            Box(                    //add button box
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxSize()
                    .padding(5.dp)
            ) {
                Button(
                    onClick = {
                        dateDialogState.show()
                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier
                    = Modifier.fillMaxSize(),
                    enabled = isButtonEnabled1
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                          //  Icons.Filled.DateRange,
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Clock icon",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(100.dp)
                        )
                    }
                }
            }

        }
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
                Button(
                    modifier = Modifier
                        .size(width = 220.dp, height = 50.dp),
                    shape = RoundedCornerShape(15.dp),
                    enabled =
                    isButtonEnabled1 && isButtonEnabled2,
                    onClick = {
                        showDialog.value = true
                        // second confirmation
                    }) {
                    Text(
                        text = "Confirm pick up",
                        fontSize = 20.sp
                    )
                }
        }
    }
    if(showDialog.value) {
        InfoDialog(sharedViewModel, context)
        showDialog.value= false
    }
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "OK") {
                timeDialogState.show()
            }
            negativeButton(text= "Cancel") {}
        }

    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title       = "Pick a date",
            allowedDateValidator = {date ->
                val today = LocalDate.now()
                date >= today
            }
        ){
            pickedDate= it
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = "OK"){
            }
            negativeButton(text= "Cancel")
        }
    ) {
        val currentTime = LocalTime.now()
        val today = LocalDate.now()

        timepicker(
            initialTime = if (pickedDate == today) currentTime.plusMinutes(5) else LocalTime.NOON,
            title       = "Pick a time",
            timeRange = if (pickedDate == today) currentTime.plusMinutes(5)..LocalTime.MAX else LocalTime.MIDNIGHT..LocalTime.MAX,
        ){
            pickedTime= it
            isButtonEnabled2=true
            val dateAndTimeField ="$formattedDate, $formattedTime"
            sharedViewModel.setDateAndTime(dateAndTimeField)
        }
    }


}


@Composable
fun MapView(context: Context,navController: NavHostController, sharedViewModel: SharedViewModel){

    // var defaultLocation=  LatLng(33.8938,35.5018)

    var pickUpMarkerState by remember {
        mutableStateOf(false)
    }
    var targetMarkerState by remember {
        mutableStateOf(false)
    }
    val uiSetting by remember { mutableStateOf(MapUiSettings()) }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val currentPosition by remember { mutableStateOf(LatLng(33.8938,35.5018)) }

    var pickUpLatLng by remember {
        mutableStateOf( LatLng(33.8938,35.5018))
    }
    var pickUpTitle by remember{
        mutableStateOf("Pick up location")
    }

    var targetLatLng by remember {
        mutableStateOf( LatLng(33.8938,35.5018))
    }
    var targetTitle by remember{
        mutableStateOf("Where to?")
    }
    var mainButtonState by remember {
        mutableStateOf("Set Pick Up location")
    }

    val cameraPosition= rememberCameraPositionState{
        position= CameraPosition.fromLatLngZoom(currentPosition,13f)
    }

    var distanceAlpha by remember {
        mutableStateOf(0.5f)
    }

    var distance by remember{
        mutableStateOf(0.0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp) // Add padding to adjust the button position
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPosition,
            properties = properties,
            uiSettings = uiSetting.copy(zoomControlsEnabled = false)
        ){
            //pick up marker
            Marker(
                state = MarkerState(position = pickUpLatLng),
                title= pickUpTitle,
                visible = pickUpMarkerState
            )
            //target market
            Marker(
                state = MarkerState(position = targetLatLng),
                title= targetTitle,
                visible = targetMarkerState
            )
        }
        Column (
            modifier = Modifier.fillMaxSize(),
          //  horizontalAlignment = Alignment.CenterHorizontally,
        ){
            //display pick up details
            Box(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                // row for pick up location and cancel button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pickUpTitle.ifEmpty { "Pick up location" },
                        modifier = Modifier.weight(0.9f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(
                        onClick = {
                            pickUpTitle= "Pick up location"
                            pickUpMarkerState= false
                            mainButtonState ="Set Pick Up location"
                            distance= 0.0
                            distanceAlpha= 0.5f
                        },
                        modifier = Modifier
                            .weight(0.1f)
                            .size(22.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = com.example.pickmeup.R.drawable.cancel_icon),
                            contentDescription = "Cancel",
                        )
                    }
                }
            }
            //display target details
            Box(
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)

            ) {
                // row for target location and cancel button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = targetTitle.ifEmpty {  "Where to?" },
                        modifier = Modifier.weight(0.9f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black

                    )
                    IconButton(
                        onClick = {
                            targetTitle ="Where to?"
                            targetMarkerState= false
                            if(mainButtonState!="Set Pick Up location"){
                                mainButtonState = "Set Target location"
                                distance= 0.0
                                distanceAlpha=0.5f
                            }
                        },
                        modifier = Modifier
                            .weight(0.1f)
                            .size(22.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = com.example.pickmeup.R.drawable.cancel_icon),
                            contentDescription = "Cancel",
                        )
                    }
                }

            }
            Row(
                modifier = Modifier
                    .alpha(distanceAlpha)
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = if (distance == 0.0) "distance:" else "distance: $distance Km",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        // center marker
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            IconButton(onClick = {   }) {
                Image(
                    painter = painterResource(id = com.example.pickmeup.R.drawable.pin3),
                    contentDescription = "marker",
                )
            }
        }

        // go to my location button

        Column (
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ){
            IconButton(
                modifier = Modifier.size(45.dp),
                onClick = {
                    getCurrentLocation(context,
                        { latitude, longitude ->

                            cameraPosition.move(
                                CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 13f)
                            )
                        }
                    ) {
                        // Call the function WhenItIsNull here
                        WhenItIsNull(context)
                    }
                }) {
                Image(
                    painter = painterResource(id = com.example.pickmeup.R.drawable.aim),
                    contentDescription = "Get Current Location",

                    )
            }
        }

        // search location button

        Column (
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 180.dp)
        ){
            IconButton(
                modifier = Modifier.size(45.dp),
                onClick = {

                }) {
                Image(
                    painter = painterResource(id = com.example.pickmeup.R.drawable.search2),
                    contentDescription = "Get Current Location",

                    )
            }
        }

        // centered button ( set pick up, target, confirm)
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .size(width = 220.dp, height = 50.dp),

                shape = RoundedCornerShape(15.dp),
                onClick = {

                    if (mainButtonState == "Set Pick Up location") {
                        pickUpLatLng = LatLng(
                            cameraPosition.position.target.latitude - 0.00001, cameraPosition.position.target.longitude
                        )
                        // avoid null geolocation
                        if (reverseGeocode(pickUpLatLng, context).isNullOrEmpty()) {
                            ShowWifiProblemDialog(context)
                        } else {
                            pickUpTitle = reverseGeocode(pickUpLatLng, context).toString()
                            pickUpMarkerState = true
                            mainButtonState = if (targetTitle == "Where to?")
                                "Set Target location"
                            else
                                "Confirm pick up"
                        }
                    }
                    else if (mainButtonState == "Set Target location") {
                        targetLatLng = LatLng(cameraPosition.position.target.latitude - 0.00001, cameraPosition.position.target.longitude)
                        if(reverseGeocode(targetLatLng, context).isNullOrEmpty()){
                            ShowWifiProblemDialog(context)
                        }
                        else {
                            targetTitle = reverseGeocode(targetLatLng, context).toString()

                            targetMarkerState = true

                            if(targetTitle=="Pick up location")
                                mainButtonState = "Set Pick Up location"
                            else {
                                mainButtonState =  "Confirm pick up"
                                distanceAlpha= 1f
                                distance= calculateDistance(pickUpLatLng, targetLatLng)
                            }
                        }
                    } else if (mainButtonState == "Confirm pick up") {

                        sharedViewModel.setPickUpTitle(pickUpTitle)
                        sharedViewModel.setTargetTitle(targetTitle)

                        sharedViewModel.setPickUpLatLng(pickUpLatLng)
                        sharedViewModel.setTargetLatLng(targetLatLng)

                        sharedViewModel.setDistance(distance)
                        navController.navigate("pickUps")

                        Toast.makeText(context, "Confirmation", Toast.LENGTH_SHORT).show()  //confirmation
                    }

                }) {
                Text(
                    text = mainButtonState,
                    fontSize = 18.sp

                )
            }


        }
    }
}

                    //FUNCTIONS
////////////////////////////////////////////////////////////////////////////////////////////

@Composable
fun InfoDialog(sharedViewModel: SharedViewModel,context: Context) {

    val text= "Pickup Title: ${sharedViewModel.pickUpTitle.value} \n" +
            "Target Title: ${sharedViewModel.targetTitle.value} \n" +
            "Pickup Latitude: ${sharedViewModel.pickUpLatLng.value.latitude}\n" +
            "Pickup Longitude: ${sharedViewModel.pickUpLatLng.value.longitude}\n" +
            "Target Latitude: ${sharedViewModel.targetLatLng.value.latitude}\n" +
            "Target Longitude: ${sharedViewModel.targetLatLng.value.longitude}\n" +
            "Distance: ${sharedViewModel.distance.value}\n"+
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

        Toast.makeText(context,"Can't get geolocation",Toast.LENGTH_SHORT).show()
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
fun ProfileScreen(navController: NavHostController) {
    Text("profile")
}

@Composable
fun SearchScreen(navController: NavHostController) {
    Text("search screen")
}