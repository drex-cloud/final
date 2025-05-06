package com.example.juakaliconnect.screens

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.juakaliconnect.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RequestFormScreen(navController: NavHostController, artisanId: String) {
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    val clientId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("📩 Request Service", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        TextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Describe your request") }
        )

        TextField(
            value = location,
            onValueChange = { location = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your location") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            sendRequest(clientId, artisanId, description, location)
            navController.navigate(Routes.CLIENT_DASHBOARD) // ✅ Redirect to dashboard after sending
        }) {
            Text("🚀 Send Request")
        }
    }
}
fun sendRequest(clientId: String, artisanId: String, description: String, location: String) {
    val db = FirebaseFirestore.getInstance()

    val requestData = hashMapOf(
        "requesterId" to clientId,
        "artisanId" to artisanId, // ✅ Ensure correct artisan ID
        "description" to description,
        "location" to location,
        "timestamp" to System.currentTimeMillis()
    )

    db.collection("requests").add(requestData)
        .addOnSuccessListener {
            Log.d("Firestore", "✅ Request sent successfully!")
        }
        .addOnFailureListener { error ->
            Log.e("Firestore", "❌ Failed to send request: ${error.message}")
        }
}