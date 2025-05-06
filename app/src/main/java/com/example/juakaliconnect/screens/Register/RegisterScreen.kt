package com.example.juakaliconnect.screens.Register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.juakaliconnect.auth.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Client") } // Default role

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())

        Row {
            RadioButton(selected = selectedRole == "Client", onClick = { selectedRole = "Client" })
            Text("Client")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(selected = selectedRole == "Artisan", onClick = { selectedRole = "Artisan" })
            Text("Artisan")
        }

        Button(onClick = { authViewModel.registerUser(fullName, email, password, selectedRole, navController) }) {
            Text("Register")
        }
    }
}