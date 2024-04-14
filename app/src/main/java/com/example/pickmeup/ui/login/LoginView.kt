package com.example.pickmeup.ui.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.pickmeup.ui.login.ui.theme.PickMeUpTheme


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
                        composable("phone") {
                            PhoneNumber(navController, this@LoginView)
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel = viewModel<LoginViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.login),
            fontSize = 36.sp,
        )

        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = { viewModel.updatePhoneNumber(it) },
            placeholder = { Text(text = stringResource(R.string.phone_number)) }
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.updatePassword(it) },
            placeholder = { Text(text = stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            FilterChip(selected = viewModel.role == 0,
                onClick = {
                    if (viewModel.role != 0) viewModel.updateRole(0)
                },
                label = { Text("Passenger") },
                leadingIcon = {
                    if (viewModel.role == 0) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(selected = viewModel.role == 1,
                onClick = {
                    if (viewModel.role != 1) viewModel.updateRole(1)
                },
                label = { Text("Driver") },
                leadingIcon = {
                    if (viewModel.role == 1) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
        ) {
            ElevatedButton(
                onClick = { navController.navigate("register") }) {
                Text(stringResource(R.string.register))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    if (viewModel.inputsFilled()) {
                        viewModel.login()
                    }
                },
                enabled = viewModel.inputsFilled()
            ) {
                Text(stringResource(R.string.login))
            }
        }
    }
}

@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel = viewModel<RegisterViewModel>()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.register),
            fontSize = 36.sp,
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = viewModel.firstName.value, onValueChange = {
            viewModel.updateFirstName(it)
        }, placeholder = {
            Text(
                stringResource(R.string.first_name)
            )
        })
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = viewModel.lastName.value, onValueChange = {
            viewModel.updateLastName(it)
        }, placeholder = {
            Text(
                stringResource(R.string.last_name)
            )
        })

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.updatePassword(it) },
            placeholder = { Text(text = stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.confirmPassword.value,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            placeholder = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = viewModel.passwordsMatch()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            FilterChip(selected = viewModel.role.intValue == 0,
                onClick = { viewModel.updateRole(0) },
                label = { Text("Passenger") },
                leadingIcon = {
                    if (viewModel.role.intValue == 0) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(selected = viewModel.role.intValue == 1,
                onClick = {viewModel.updateRole(1)},
                label = { Text("Driver") },
                leadingIcon = {
                    if (viewModel.role.intValue == 1) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = null
                        )
                    }
                }
            )
        }
        Button(
            onClick = {
                navController.navigate("phone")
            },
            enabled = viewModel.inputsFilled()
        ) {
            Text(stringResource(R.string.register))
        }
    }
}

@Composable
fun PhoneNumber(navController: NavController, activity: ComponentActivity) {
    val viewModel = viewModel<OTPViewModel>()
    val registerViewModel = viewModel<RegisterViewModel>()
    Column(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter phone number", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.phoneNumber.value,
            onValueChange = { viewModel.updatePhoneNumber(it) },
            placeholder = { Text("Phone Number") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.authenticate(activity) }) {
            Text("Send OTP")
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.otp.value,
            onValueChange = { viewModel.updateOTP(it) },
            placeholder = { Text("OTP") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.verifyOTP() }) {
            Text("Verify OTP")
        }
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