package com.example.juakaliconnect.screens.artisans

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.juakaliconnect.navigation.Routes


@Composable
fun ArtisanDashboardScreen(navController: NavController, artisanId: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Welcome, Artisan!", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Display Service Requests
        Text(text = "Your Service Requests", fontWeight = FontWeight.Bold)
        Button(onClick = { navController.navigate(Routes.REQUESTS) }) {
            Text("View Incoming Requests")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Update Option
        Button(onClick = { navController.navigate(Routes.PROFILE_UPDATE) }) {
            Text("Update Your Profile")
        }
    }
}