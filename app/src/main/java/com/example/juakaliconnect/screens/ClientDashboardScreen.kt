package com.example.juakaliconnect.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.juakaliconnect.navigation.Routes
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Star

import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search



@Composable
fun ClientDashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Welcome, Client!", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Browse Requests
        Text(text = "Service Requests", fontWeight = FontWeight.Bold)
        Button(
            onClick = { navController.navigate(Routes.REQUESTS) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.List, contentDescription = "Requests", tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("View Requests You've Made")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Browse Artisans
        Text(text = "Find Skilled Artisans", fontWeight = FontWeight.Bold)
        Button(
            onClick = { navController.navigate("artisan_list") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Search, contentDescription = "Browse Artisans", tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Browse Artisans")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Messaging Feature
        Text(text = "Your Conversations", fontWeight = FontWeight.Bold)
        Button(
            onClick = { navController.navigate(Routes.CHAT) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Message, contentDescription = "Messages", tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Go to Messages")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reviews Section (Fixed Navigation)
        Text(text = "Artisan Reviews", fontWeight = FontWeight.Bold)
        Button(
            onClick = { navController.navigate("artisan_reviews") }, // Removed unnecessary `{artisanId}`
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Star, contentDescription = "Reviews", tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("View Reviews")
        }
    }
}