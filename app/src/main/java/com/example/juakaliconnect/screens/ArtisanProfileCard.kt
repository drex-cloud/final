package com.example.juakaliconnect.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.juakaliconnect.model.Artisan
import com.example.juakaliconnect.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


@Composable
fun ArtisanProfileCard(artisan: Artisan, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("artisanProfile/${artisan.artisanId}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(artisan.fullName, fontWeight = FontWeight.Bold)
            Text("🛠 Skills: ${artisan.skills}")
            Text("📍 Location: ${artisan.location}")
            Text("⭐ Rating: ${artisan.averageRating}")

            Spacer(modifier = Modifier.height(8.dp))

            // Reviews Preview (Limited to 3 top reviews)
            Text("📝 Top Reviews:", fontWeight = FontWeight.Bold)
            artisan.reviews.take(3).forEach { review ->
                Text("- ${review.comment} (⭐${review.rating})", fontStyle = FontStyle.Italic)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔥 Chat Button for Instant Messaging
            Button(onClick = { navController.navigate("chat_screen/${artisan.artisanId}") }) {
                Text("💬 Chat with ${artisan.fullName}")
            }
            Button(onClick = {
                navController.navigate("request_form/${artisan.artisanId}") // ✅ Use artisanId instead of id
            }) {
                Text("📩 Request Service")
            }


        }
    }
}

// 🔹 Function to Fetch Reviews
