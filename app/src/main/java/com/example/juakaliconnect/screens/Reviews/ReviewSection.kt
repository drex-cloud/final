package com.example.juakaliconnect.screens.Reviews


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore


// Review Section Composable
@Composable
fun ReviewSection(artisanId: String) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var reviews by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var newReview by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }

    // Fetch Reviews Dynamically
    LaunchedEffect(artisanId) {
        fetchArtisanReviews(artisanId) { fetchedReviews ->
            reviews = fetchedReviews
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Client Reviews", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        if (reviews.isEmpty()) {
            Text("No reviews yet. Be the first to leave one!", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn {
                items(reviews) { review ->
                    Card(modifier = Modifier.padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = review["clientName"] ?: "Unknown", fontWeight = FontWeight.Bold)
                            Text(text = review["comment"] ?: "No comment provided")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Review Input Form
        OutlinedTextField(
            value = clientName,
            onValueChange = { clientName = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = newReview,
            onValueChange = { newReview = it },
            label = { Text("Write a Review") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Submit Button with Toast Notification
        Button(
            onClick = {
                val reviewData = mapOf(
                    "clientName" to clientName,
                    "comment" to newReview,
                    "timestamp" to System.currentTimeMillis()
                )

                db.collection("users").document(artisanId).collection("reviews")
                    .add(reviewData)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Review submitted!", Toast.LENGTH_SHORT).show()
                        newReview = "" // Reset input after submitting
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(context, "Failed to submit review", Toast.LENGTH_SHORT).show()
                        println("Error submitting review: ${error.message}")
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Review")
        }
    }
}