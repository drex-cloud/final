package com.example.juakaliconnect.screens.Request

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.juakaliconnect.R

@Composable
fun RequestsScreen(navController: NavHostController) {
    val artisanId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var requests by remember { mutableStateOf<List<Request>>(emptyList()) }

    Log.d("Firestore", "Fetching requests for artisan ID: $artisanId")

    LaunchedEffect(artisanId) {
        fetchRequests(artisanId) { fetchedRequests ->
            requests = fetchedRequests
            Log.d("Firestore", "🔥 Updated UI with ${requests.size} requests")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ✅ Background Image using painterResource
        Image(
            painter = painterResource(id = R.drawable.ln),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("📥 Incoming Requests", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)

            if (requests.isEmpty()) {
                Text("No requests found.", color = Color.LightGray)
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(requests) { request ->
                    RequestCard(
                        request,
                        navController,
                        onAccept = { acceptRequest(request.id, navController, request.requesterId) },
                        onDecline = { declineRequest(request.id) }
                    )
                }
            }
        }
    }
}

// ✅ Accept Request Function
fun acceptRequest(requestId: String, navController: NavHostController, requesterId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("requests").document(requestId)
        .update("status", "accepted")
        .addOnSuccessListener {
            Log.d("Firestore", "✅ Request accepted!")
            navController.navigate("chat_screen/$requesterId") // ✅ Redirect after acceptance
        }
        .addOnFailureListener { Log.e("Firestore", "❌ Failed to accept request: ${it.message}") }
}

fun declineRequest(requestId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("requests").document(requestId)
        .update("status", "declined")
        .addOnSuccessListener { Log.d("Firestore", "❌ Request declined!") }
        .addOnFailureListener { Log.e("Firestore", "❌ Failed to decline request: ${it.message}") }
}

// 🔥 Updated RequestCard with Accept & Decline Buttons

fun fetchRequests(artisanId: String, onResult: (List<Request>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("requests")
        .whereIn("status", listOf("pending", "accepted"))
        .whereEqualTo("artisanId", artisanId)
        .orderBy("timestamp", Query.Direction.ASCENDING)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Firestore", "❌ Listener error: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val requests = snapshot.documents.map { doc ->
                    Request(
                        id = doc.id,
                        requesterId = doc.getString("requesterId").orEmpty(),
                        requesterName = doc.getString("requesterName").orEmpty(),
                        description = doc.getString("description").orEmpty(),
                        location = doc.getString("location").orEmpty(),
                        status = doc.getString("status").orEmpty()
                    )
                }
                Log.d("Firestore", "🔥 Live requests loaded: $requests")
                onResult(requests)
            } else {
                Log.d("Firestore", "⚠ No pending or accepted requests found.")
            }
        }
}