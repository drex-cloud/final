package com.example.juakaliconnect.screens.Reviews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

@Composable
fun AddReviewScreen(navController: NavHostController, artisanId: String) {
    val db = FirebaseFirestore.getInstance()
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Leave a Review", style = MaterialTheme.typography.headlineMedium)

        TextField(
            value = reviewText,
            onValueChange = { reviewText = it },
            label = { Text("Write your feedback") },
            modifier = Modifier.fillMaxWidth()
        )

        Row {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = "Rating",
                    modifier = Modifier
                        .clickable { rating = index + 1 }
                        .padding(4.dp)
                )
            }
        }

        Button(
            onClick = {
                val review = hashMapOf(
                    "artisanId" to artisanId,
                    "comment" to reviewText,
                    "rating" to rating,
                    "timestamp" to System.currentTimeMillis()
                )

                db.collection("reviews").add(review)
                    .addOnSuccessListener {
                        Log.d("Review", "Review added successfully!")

                        // Update average rating
                        val artisanRef = db.collection("users").document(artisanId)
                        db.collection("reviews")
                            .whereEqualTo("artisanId", artisanId)
                            .get()
                            .addOnSuccessListener { documents ->
                                val totalReviews = documents.size()
                                val totalRating = documents.sumOf {
                                    it["rating"]?.toString()?.toIntOrNull() ?: 0
                                }
                                val newAverage = if (totalReviews > 0)
                                    totalRating.toFloat() / totalReviews
                                else 0.0f

                                artisanRef.update("averageRating", newAverage)
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "Average rating updated!")
                                    }
                                    .addOnFailureListener { error ->
                                        Log.e("Firestore", "Error updating rating: ${error.message}")
                                    }
                            }

                        navController.popBackStack()
                    }
                    .addOnFailureListener { error ->
                        Log.e("Review", "Error adding review: ${error.message}")
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Review")
        }
    }
}
