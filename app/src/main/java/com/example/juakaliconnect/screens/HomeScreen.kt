package com.example.juakaliconnect.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.juakaliconnect.auth.AuthViewModel
import com.example.juakaliconnect.navigation.Routes

@Composable
fun HomeScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Artisan Connect", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Call-to-action section
        Button(onClick = { navController.navigate(Routes.REGISTER) }) {
            Text("Get Started")
        }

        TextButton(onClick = { navController.navigate(Routes.LOGIN) }) {
            Text("Already have an account? Log in")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured Artisans (Placeholder)
        Text(text = "Top Artisans", fontWeight = FontWeight.Bold)
        Button(onClick = { navController.navigate(Routes.ARTISAN_LIST) }) {
            Text("Browse Artisans")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Requests (Placeholder)
        Text(text = "Recent Service Requests", fontWeight = FontWeight.Bold)
        Button(onClick = { navController.navigate(Routes.REQUESTS) }) {
            Text("View Requests")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Testimonials Section
        Text(text = "What Our Users Say", fontWeight = FontWeight.Bold)
        Button(onClick = { navController.navigate(Routes.TESTIMONIALS) }) {
            Text("Read Reviews")
        }
    }
}