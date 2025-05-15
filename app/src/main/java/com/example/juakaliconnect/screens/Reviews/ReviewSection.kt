package com.example.juakaliconnect.screens.Reviews


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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
@Composable
fun ReviewSection(artisanId: String?) {
    val db = FirebaseFirestore.getInstance()
    var reviews by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var averageRating by remember { mutableStateOf(0f) }

    if (artisanId.isNullOrEmpty()) {
        Log.e("Navigation", "❌ Invalid Artisan ID detected!")
        Text("⚠️ Error: Invalid Artisan ID", color = Color.Red)
        return
    }

    // 🔥 Fetch Live Reviews & Update Instantly
    LaunchedEffect(artisanId) {
        db.collection("reviews").whereEqualTo("artisanId", artisanId).addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Firestore", "❌ Error Fetching Reviews: ${error.message}")
                return@addSnapshotListener
            }

            snapshots?.documents?.takeIf { it.isNotEmpty() }?.let { docs ->
                reviews = docs.map { doc -> doc.data?.mapValues { it.value.toString() } ?: emptyMap() }
                Log.d("Firestore", "✅ Live Reviews Updated: ${reviews.size} reviews fetched")

                // 🔥 Calculate Average Rating Safely
                val totalRating = reviews.sumOf { it["rating"]?.toIntOrNull() ?: 0 }
                averageRating = (if (reviews.isNotEmpty()) totalRating.toFloat() / reviews.size else 0f).coerceAtLeast(0f)
                Log.d("Firestore", "✅ Calculated Average Rating: $averageRating")
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("⭐ Client Reviews", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        Text("⭐ Average Rating: ${String.format("%.1f", averageRating)}", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 600.dp)
        ) {
            items(reviews) { review ->
                if (review["comment"].isNullOrEmpty()) return@items // ✅ Prevents empty reviews from displaying
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
}