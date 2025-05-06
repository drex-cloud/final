package com.example.juakaliconnect.screens.artisans

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController

@Composable
fun ConversationCard(conversation: Map<String, String>, navController: NavHostController) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("💬 Chat with: ${conversation["requesterName"]}", fontWeight = FontWeight.Bold)

            Button(onClick = {
                navController.navigate("chat_screen/${conversation["requesterId"]}") // ✅ Navigate to chat
            }) {
                Text("🔹 Open Chat")
            }
        }
    }
}