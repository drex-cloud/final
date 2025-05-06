package com.example.juakaliconnect.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@Composable
fun RequestsScreen(navController: NavHostController) {
    val artisanId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var requests by remember { mutableStateOf<List<Request>>(emptyList()) }

    Log.d("Firestore", "Fetching requests for artisan ID: $artisanId")

    // Fetch requests dynamically with enhanced debugging
    LaunchedEffect(artisanId) {
        fetchRequests(artisanId) { fetchedRequests ->
            requests = fetchedRequests
            Log.d("Firestore", "🔥 Updated UI with ${requests.size} requests")
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("📥 Incoming Requests", fontSize = 20.sp)

        if (requests.isEmpty()) {
            Text("No requests found.", color = Color.Gray)
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(requests) { request ->
                RequestCard(request, navController)
            }
        }
    }
}

// 🔥 Optimized Firestore Query to Fix Missing Requests
fun fetchRequests(artisanId: String, onResult: (List<Request>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("requests")
        .whereEqualTo("artisanId", artisanId) // ✅ Ensure correct filtering
        .orderBy("timestamp", Query.Direction.ASCENDING) // ✅ Requires indexing
        .get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                Log.d("Firestore", "⚠ No requests found for artisan $artisanId")
            } else {
                Log.d("Firestore", "✅ Found ${snapshot.documents.size} requests")
            }

            val requests = snapshot?.documents?.map {
                Request(
                    requesterId = it.getString("requesterId").orEmpty(),
                    requesterName = it.getString("requesterName").orEmpty(),
                    description = it.getString("description").orEmpty(),
                    location = it.getString("location").orEmpty()
                )
            } ?: emptyList()

            Log.d("Firestore", "🔥 Requests successfully loaded: $requests")
            onResult(requests)
        }
        .addOnFailureListener { error ->
            Log.e("Firestore", "❌ Error fetching requests: ${error.message}")
        }
}