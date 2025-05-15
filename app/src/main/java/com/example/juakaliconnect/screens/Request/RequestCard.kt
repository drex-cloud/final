package com.example.juakaliconnect.screens.Request

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun RequestCard(request: Request, navController: NavHostController, onAccept: (String) -> Unit, onDecline: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // ✅ Light blue background for visibility
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🔹 Request from: ${request.requesterName}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("📜 Job Details: ${request.description}")
            Text("📍 Location: ${request.location}")

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                "📌 Status: ${request.status.uppercase()}",
                color = when (request.status) {
                    "pending" -> Color.Gray
                    "accepted" -> Color.Green
                    "declined" -> Color.Red
                    else -> Color.Black
                },
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (request.status == "pending") {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onAccept(request.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("✅ Accept")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onDecline(request.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("❌ Decline")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (request.status == "accepted") {
                Button(
                    onClick = { navController.navigate("chat_screen/${request.requesterId}") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("💬 Chat with Requester")
                }
            }
        }
    }
}

// 🔥 Request Data Model
data class Request(
    val id: String = "",
    val requesterId: String,
    val requesterName: String,
    val description: String,
    val location: String,
    val status: String = "pending" // New field to track acceptance
)
