package com.example.pickmeup.ui.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.pickmeup.ui.login.ui.theme.PickMeUpTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pickmeup.R


class LoginView : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PickMeUpTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("register") {
                            RegisterScreen(navController)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun LoginScreen(navController: NavController) {

    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.login),
            fontSize = 32.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.phone_number),
                )
            })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = {
            password = it
        }, placeholder = {
            Text(
                text = stringResource(
                    R.string.password
                )
            )
        },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
        ) {
            ElevatedButton(onClick = { navController.navigate("register") }) {
                Text(stringResource(R.string.register))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { /*TODO*/ }) {
                Text(stringResource(R.string.login))
            }
        }
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.register),
            fontSize = 32.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = firstName, onValueChange = {
            firstName = it
        }, placeholder = {
            Text(
                stringResource(R.string.first_name)
            )
        })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = lastName, onValueChange = {
            lastName = it
        }, placeholder = {
            Text(
                stringResource(R.string.last_name)
            )
        })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, placeholder = {
            Text("Password")
        })
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
@Preview(showBackground = true)
fun LoginPreview() {
    PickMeUpTheme {
        LoginScreen(rememberNavController())
    }
}

@Composable
@Preview(showBackground = true)
fun RegisterPreview() {
    PickMeUpTheme {
        RegisterScreen(rememberNavController())
    }
}