package com.example.juakaliconnect.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun RequestCard(request: Request, navController: NavHostController) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🔹 Request from: ${request.requesterName}", fontWeight = FontWeight.Bold)
            Text("📜 Job Details: ${request.description}")
            Text("📍 Location: ${request.location}")

            Spacer(modifier = Modifier.height(8.dp))

            // Chat Button for Direct Messaging
            Button(onClick = {
                navController.navigate("chat_screen/${request.requesterId}")
            }) {
                Text("💬 Chat with Requester")
            }
        }
    }
}

// 🔥 Request Data Model
data class Request(
    val requesterId: String,
    val requesterName: String,
    val description: String,
    val location: String
)