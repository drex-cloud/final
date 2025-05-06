package com.example.juakaliconnect.screens.Reviews

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(navController: NavHostController, artisanId: String) {
    val db = FirebaseFirestore.getInstance()
    var reviews by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var newReview by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }

    // Fetch Reviews
    LaunchedEffect(artisanId) {
        db.collection("users").document(artisanId).collection("reviews").get()
            .addOnSuccessListener { documents ->
                reviews = documents.map { documentSnapshot ->
                    documentSnapshot.data?.mapValues { it.value.toString() } ?: emptyMap()
                }
                Log.d("Firestore", "Fetched reviews: $reviews") // ✅ Debugging log
            }
            .addOnFailureListener { error ->
                Log.e("Firestore", "Error fetching reviews: ${error.message}")
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Reviews for Artisan") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("Client Reviews", style = MaterialTheme.typography.headlineMedium)

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

                // Submit Button
                Button(
                    onClick = {
                        val reviewData = mapOf("clientName" to clientName, "comment" to newReview)
                        db.collection("users").document(artisanId).collection("reviews").add(reviewData)
                        newReview = "" // Reset input after submitting
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Review")
                }
            }
        }
    )
}