package com.example.juakaliconnect.screens.Reviews

import com.example.juakaliconnect.R
import android.content.Context
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
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
@Composable
fun AddReviewScreen(navController: NavHostController, artisanId: String, context: Context) {
    val db = FirebaseFirestore.getInstance()
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 🔥 Background Image
        Image(
            painter = painterResource(id = R.drawable.qo), // ✅ Ensure `qo.jpg` or `ni.jpg` exists in `res/drawable/`
            contentDescription = "Review Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🔥 Overlay for readability
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)) // ✅ Adds transparency for better visibility
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // ✅ Enables scrolling if content exceeds screen size
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("⭐ Leave a Review", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(12.dp))

            // 🔥 Review Input Field
            TextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                label = { Text("Write your feedback") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔥 Updated Star Rating UI with Better Visibility
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Rating",
                        tint = if (index < rating) Color(0xFFFFD700) else Color(0xFFB0BEC5), // ✅ Gold for selected, Light Gray for unselected
                        modifier = Modifier
                            .size(45.dp) // ✅ Increased size for better tap experience
                            .clickable { rating = index + 1 }
                            .padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔥 Submit Button Always Visible
            Button(
                onClick = {
                    sendReview(context, navController, artisanId, reviewText, rating)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C))
            ) {
                Text("🚀 Submit Review", color = Color.White)
            }
        }
    }
}

// 🔥 Updated Review Submission with Toast
fun sendReview(context: Context, navController: NavHostController, artisanId: String, reviewText: String, rating: Int) {
    val db = FirebaseFirestore.getInstance()

    val review = hashMapOf(
        "artisanId" to artisanId,
        "comment" to reviewText,
        "rating" to rating,
        "timestamp" to System.currentTimeMillis()
    )

    db.collection("reviews").add(review)
        .addOnSuccessListener {
            Log.d("Review", "✅ Review added successfully!")
            Toast.makeText(context, "Review submitted! 🎉", Toast.LENGTH_SHORT).show() // ✅ Success message

            // 🔥 Update Artisan's Average Rating
            val artisanRef = db.collection("users").document(artisanId)
            db.collection("reviews")
                .whereEqualTo("artisanId", artisanId)
                .get()
                .addOnSuccessListener { documents ->
                    val totalReviews = documents.size()
                    val totalRating = documents.sumOf { it["rating"]?.toString()?.toIntOrNull() ?: 0 }
                    val newAverage = if (totalReviews > 0) totalRating.toFloat() / totalReviews else 0.0f

                    artisanRef.update("averageRating", newAverage)
                        .addOnSuccessListener {
                            Log.d("Firestore", "✅ Average rating updated successfully!")
                            Toast.makeText(context, "Average rating updated! ✅", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { error ->
                            Log.e("Firestore", "❌ Error updating rating: ${error.message}")
                        }
                }

            navController.popBackStack()
        }
        .addOnFailureListener { error ->
            Log.e("Review", "❌ Error adding review: ${error.message}")
            Toast.makeText(context, "Failed to submit review! ❌", Toast.LENGTH_SHORT).show() // ✅ Error message
        }
}