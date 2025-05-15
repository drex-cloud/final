package com.example.juakaliconnect.screens.Request

import com.example.juakaliconnect.R
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.juakaliconnect.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RequestFormScreen(navController: NavHostController, artisanId: String, context: Context) {
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val clientId = auth.currentUser?.uid.orEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        // 🔥 Background Image Implementation
        Image(
            painter = painterResource(id = R.drawable.ln), // ✅ Ensure `qo.jpg` is inside `res/drawable/`
            contentDescription = "Request Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text("📩 Request Service", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)

            TextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Describe your request") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your location") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                sendRequest(context, clientId, artisanId, description, location)
            }) {
                Text("🚀 Send Request")
            }
        }
    }
}

// 🔥 Updated Send Request Function with Toast Message
fun sendRequest(context: Context, clientId: String, artisanId: String, description: String, location: String) {
    val db = FirebaseFirestore.getInstance()

    val requestData = hashMapOf(
        "requesterId" to clientId,
        "artisanId" to artisanId,
        "description" to description,
        "location" to location,
        "status" to "pending",
        "timestamp" to System.currentTimeMillis()
    )

    db.collection("requests").add(requestData)
        .addOnSuccessListener {
            Log.d("Firestore", "✅ Request sent successfully!")
            Toast.makeText(context, "Request sent successfully! 🎉", Toast.LENGTH_SHORT).show() // ✅ Toast message
        }
        .addOnFailureListener { error ->
            Log.e("Firestore", "❌ Failed to send request: ${error.message}")
            Toast.makeText(context, "Failed to send request! ❌", Toast.LENGTH_SHORT).show() // ✅ Toast for error
        }
}