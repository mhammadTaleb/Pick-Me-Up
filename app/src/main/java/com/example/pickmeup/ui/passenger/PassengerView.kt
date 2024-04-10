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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
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


data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class PassengerView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PickMeUpTheme {
                val items = listOf(
                    BottomNavigationItem("search", Icons.Filled.Search, Icons.Outlined.Search),
                    BottomNavigationItem("home", Icons.Filled.Home, Icons.Outlined.Home),
                    BottomNavigationItem("profile", Icons.Filled.Person, Icons.Outlined.Person)
                )
                // Initialize selectedItemIndex to the index of the "home" item
                var selectedItemIndex by remember { mutableStateOf(items.indexOfFirst { it.title == "home" }) }
                val navController = rememberNavController()

                // A surface container using the 'background' color from the theme
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
                                    HomeScreen(navController, this@PassengerView)
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
fun HomeScreen(navController: NavHostController, context: Context) {

    // MapDemo(context = context)
    PickUps(context = context, navController)

    TimeAndDatePicker(context = context)

}


@Composable
fun TimeAndDatePicker(context: Context) {

    var pickedDate by remember {
        mutableStateOf(LocalDate.now())
    }
    var pickedTime by remember{
        mutableStateOf(LocalTime.NOON)
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

    Column(
        modifier= Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(onClick = {
            dateDialogState.show()
        }) {
            Text(text = "Pick date")
        }
        Text(text = formattedDate)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            timeDialogState.show()
        }) {
            Text(text = "Pick time")
        }
        Text(text = formattedTime)
    }

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "OK"){

            }
            negativeButton(text= "Cancel")
        }
    ) {
        datepicker(
            initialDate = LocalDate.now(),
            title       = "Pick a date",
            allowedDateValidator = {
                // remove the date that are more that one month from now
                it.dayOfMonth > 0
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
        timepicker(
            initialTime = LocalTime.NOON,
            title       = "Pick a time",
            timeRange = LocalTime.MIDNIGHT..LocalTime.NOON
        ){
            pickedTime= it
        }
    }
}

@Composable
fun PickUps(context: Context, navController: NavHostController){

    val (showMapComposable, setShowMapComposable) = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), // Use fillMaxWidth for the Row
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Request new Pick UP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(top = 16.dp)
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
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
                            .padding(start = 15.dp, end = 15.dp, top = 15.dp)
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
                                text = "Enter pick up location",
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                    Box(                    //target location box
                        modifier = Modifier
                            .padding(start = 15.dp, end = 15.dp, top = 15.dp)
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
                                text = "Enter destination",
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
                        setShowMapComposable(!showMapComposable)
                    },
                    shape = MaterialTheme.shapes.medium, // Set the button shape to medium (cubic)
                    modifier = Modifier.fillMaxSize(), // Fill the entire available space in the box
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Clock icon",
                            modifier = Modifier.padding(end = 8.dp) // Add padding to the right of the icon
                        )
                    }
                }
            }
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
                            .padding(start = 15.dp, end = 15.dp, top = 15.dp)
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
                                text = "Set time",                  // time text
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
                        //setShowMapComposable(!showMapComposable)

                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier
                    = Modifier.fillMaxSize(),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                       /* Image(
                            painter = painterResource(id = com.example.pickmeup.R.drawable.calendar ),
                            contentDescription = "date",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(42.dp)
                                
                        ) */
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



    }



    if (showMapComposable) {
        MapView(context, navController)
    }
}



@Composable
fun MapView(context: Context,navController: NavHostController){

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
    val (showMainComposable, setShowMainComposable) = remember { mutableStateOf(false) }


    val cameraPosition= rememberCameraPositionState{
        position= CameraPosition.fromLatLngZoom(currentPosition,13f)
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
            horizontalAlignment = Alignment.CenterHorizontally,
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

        }   // center marker
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
                        if (ReverseGeocode(pickUpLatLng, context).isNullOrEmpty()) {
                            ShowWifiProblemDialog(context)
                        } else {
                            pickUpTitle = ReverseGeocode(pickUpLatLng, context).toString()
                            pickUpMarkerState = true
                            if (targetTitle == "Where to?")
                                mainButtonState = "Set Target location"
                            else
                                mainButtonState = "Confirm pick up"
                        }
                    }
                    else if (mainButtonState == "Set Target location") {
                        targetLatLng = LatLng(cameraPosition.position.target.latitude - 0.00001, cameraPosition.position.target.longitude)
                        if(ReverseGeocode(targetLatLng, context).isNullOrEmpty()){
                            ShowWifiProblemDialog(context)
                        }
                        else {
                            targetTitle = ReverseGeocode(targetLatLng, context).toString()

                            targetMarkerState = true
                            if(targetTitle=="Pick up location")
                                mainButtonState = "Set Pick Up location"
                            else
                                mainButtonState= "Confirm pick up"
                        }
                    } else if (mainButtonState == "Confirm pick up") {
                        setShowMainComposable(!showMainComposable)


                        Toast.makeText(context, "Confirmation", Toast.LENGTH_SHORT).show()
                    }

                }) {
                Text(
                    text = mainButtonState,
                    fontSize = 18.sp

                )
            }


            if (showMainComposable) {

                navController.navigate("home")
            }

        }
    }
}

fun ReverseGeocode(latlng: LatLng, context: Context): String? {
    var latitude = latlng.latitude
    val longitude = latlng.longitude

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
       // Log.i("xxxx", "if statement")
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
    // write a lazycolumn list of 100 items (list $number)
    Text("search screen")
}