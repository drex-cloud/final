package com.example.juakaliconnect.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.juakaliconnect.components.CustomActionButton
import com.example.juakaliconnect.data.HireRepository



@Composable
fun HireRequestScreen(navController: NavHostController, artisanId: String, clientId: String) {
    val hireRepo = HireRepository()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Hire Artisan", style = MaterialTheme.typography.headlineSmall)
        Text("Confirm your service request.")

        CustomActionButton("Confirm Hire") {
            hireRepo.createHireRequest(clientId, artisanId, "Plumbing") { success ->
                if (success) navController.navigate("home_screen") // ✅ Redirect after hiring
            }
        }
    }
}