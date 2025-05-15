package com.example.juakaliconnect.screens.Reviews

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(navController: NavHostController, artisanId: String?, context: Context) {
    val db = FirebaseFirestore.getInstance()
    var reviews by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var newReview by remember { mutableStateOf("") }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var clientName by remember { mutableStateOf("Anonymous") }
    var rating by remember { mutableStateOf(0) }

    if (artisanId.isNullOrEmpty()) {
        Log.e("Navigation", "❌ Invalid Artisan ID detected!")
        Text("⚠️ Error: Invalid Artisan ID", color = Color.Red)
        return
    }

    // 🔥 Fetch Client Name Automatically
    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    clientName = document.getString("fullName") ?: "Anonymous"
                    Log.d("Firestore", "✅ Fetched Client Name: $clientName")
                }
                .addOnFailureListener { error ->
                    Log.e("Firestore", "❌ Failed to fetch client name: ${error.message}")
                }
        }
    }

    // 🔥 Fetch Live Reviews (Updates Instantly on New Submission)
    LaunchedEffect(artisanId) {
        db.collection("reviews").whereEqualTo("artisanId", artisanId).addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Firestore", "❌ Error Fetching Reviews: ${error.message}")
                return@addSnapshotListener
            }
            snapshots?.let {
                reviews = it.documents.map { doc -> doc.data?.mapValues { it.value.toString() } ?: emptyMap() }
                Log.d("Firestore", "✅ Live Reviews Updated: $reviews")
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Reviews for Artisan", fontWeight = FontWeight.Bold) }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
        ) {
            Text("⭐ Client Reviews", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))

            if (reviews.isEmpty()) {
                Text("No reviews yet. Be the first to leave one!", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 600.dp)
                ) {
                    items(reviews) { review ->
                        Card(modifier = Modifier.padding(8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = review["clientName"] ?: "Unknown", fontWeight = FontWeight.Bold)
                                Text(text = review["comment"] ?: "No comment provided")

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.Start) {
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "Rating",
                                            tint = if (index < (review["rating"]?.toString()?.toIntOrNull() ?: 0)) Color(0xFFFFD700) else Color.LightGray,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔥 Review Input Form
            OutlinedTextField(
                value = newReview,
                onValueChange = { newReview = it },
                label = { Text("Write a Review") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Rating",
                        tint = if (index < rating) Color(0xFFFFD700) else Color.LightGray,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { rating = index + 1 }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val reviewData = mapOf(
                        "artisanId" to artisanId,
                        "clientName" to clientName,
                        "comment" to newReview,
                        "rating" to rating.toString(),
                        "timestamp" to System.currentTimeMillis().toString()
                    )

                    db.collection("reviews").add(reviewData)
                        .addOnSuccessListener {
                            Log.d("Firestore", "✅ Review Submitted Successfully: $reviewData")
                            Toast.makeText(context, "Review submitted! 🎉", Toast.LENGTH_SHORT).show()
                            newReview = "" // ✅ Reset input
                            rating = 0 // ✅ Reset rating
                        }
                        .addOnFailureListener { error ->
                            Log.e("Firestore", "❌ Error Submitting Review: ${error.message}")
                            Toast.makeText(context, "Failed to submit review! ❌", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C))
            ) {
                Text("🚀 Submit Review", color = Color.White)
            }
        }
    }
}