package com.example.juakaliconnect.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import com.example.juakaliconnect.screens.Reviews.fetchArtisanReviews

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ArtisanProfileScreen(navController: NavHostController, artisanId: String) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var artisanData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var currentUserRole by remember { mutableStateOf("") }
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    var reviews by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }

    // Fetch artisan profile
    LaunchedEffect(artisanId) {
        db.collection("users").document(artisanId).get()
            .addOnSuccessListener { document ->
                artisanData = document.data
            }
            .addOnFailureListener { error ->
                Log.e("Firestore", "Error fetching profile: ${error.message}")
            }
    }

    // Fetch user role
    LaunchedEffect(Unit) {
        currentUserId?.let { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    currentUserRole = document.getString("role") ?: ""
                }
                .addOnFailureListener { error ->
                    Log.e("Firestore", "Error fetching user role: ${error.message}")
                }
        }
    }

    // Fetch reviews dynamically
    LaunchedEffect(artisanId) {
        fetchArtisanReviews(artisanId) { fetchedReviews ->
            reviews = fetchedReviews
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Artisan Profile", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            artisanData?.let { artisan ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("👤 ${artisan["fullName"] ?: "N/A"}", style = MaterialTheme.typography.titleLarge)
                        Text("📍 ${artisan["location"] ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                        Text("🛠 ${artisan["skills"] ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                        Text("⭐ Average Rating: ${artisan["averageRating"] ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Client Reviews", style = MaterialTheme.typography.titleMedium)

            Box(modifier = Modifier.weight(1f)) { // Ensures list takes flexible space
                LazyColumn {
                    items(reviews) { review ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(review["clientName"] ?: "Anonymous", fontWeight = FontWeight.Bold)
                                Text(review["comment"] ?: "No comment provided")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Make sure the review form is always visible
            if (currentUserRole == "Client") {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Leave a Review", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        TextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            label = { Text("Write your feedback") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = if (index < rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "Rating",
                                    modifier = Modifier.clickable { rating = index + 1 }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val review = mapOf(
                                    "clientName" to (artisanData?.get("fullName") ?: "Client"),
                                    "comment" to reviewText,
                                    "rating" to rating,
                                    "timestamp" to System.currentTimeMillis()
                                )

                                db.collection("users").document(artisanId).collection("reviews")
                                    .add(review)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
                                        updateAverageRating(artisanId, db)
                                        fetchArtisanReviews(artisanId) { fetched ->
                                            reviews = fetched
                                        }
                                        reviewText = ""
                                        rating = 0
                                    }
                                    .addOnFailureListener { error ->
                                        Toast.makeText(context, "Review submission failed", Toast.LENGTH_SHORT).show()
                                        Log.e("Review", "Error adding review: ${error.message}")
                                    }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit Review")
                        }
                    }
                }
            }
        }
    }
}
fun updateAverageRating(artisanId: String, db: FirebaseFirestore) {
    val artisanRef = db.collection("users").document(artisanId)

    db.collection("users").document(artisanId).collection("reviews").get()
        .addOnSuccessListener { documents ->
            val totalReviews = documents.size()
            val totalRating = documents.sumOf { it["rating"]?.toString()?.toIntOrNull() ?: 0 }
            val newAverage = if (totalReviews > 0) totalRating.toFloat() / totalReviews else 0.0f

            artisanRef.update("averageRating", newAverage)
                .addOnSuccessListener { Log.d("Firestore", "✅ Average rating updated successfully!") }
                .addOnFailureListener { error -> Log.e("Firestore", "❌ Error updating rating: ${error.message}") }
        }
        .addOnFailureListener { error -> Log.e("Firestore", "❌ Failed to fetch reviews: ${error.message}") }
}