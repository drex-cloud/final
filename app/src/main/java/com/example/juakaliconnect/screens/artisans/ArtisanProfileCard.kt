package com.example.juakaliconnect.screens.artisans

import android.util.Log
import com.example.juakaliconnect.R
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.juakaliconnect.model.Artisan
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ArtisanProfileCard(artisan: Artisan, navController: NavHostController) {
    val defaultProfilePic = "https://i.imgur.com/3fVBF3s.jpeg" // ✅ Default placeholder image
    val profilePicUrl = artisan.profilePicUrl.takeIf { it.isNotEmpty() } ?: defaultProfilePic // ✅ Ensures valid image
    val db = FirebaseFirestore.getInstance()

    var averageRating by remember { mutableStateOf(artisan.averageRating) }

    // 🔥 Fetch Live Rating Updates
    LaunchedEffect(artisan.artisanId) {
        artisan.artisanId?.let { id ->
            db.collection("reviews").whereEqualTo("artisanId", id).addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("Firestore", "❌ Error Fetching Rating: ${error.message}")
                    return@addSnapshotListener
                }
                snapshots?.documents?.takeIf { it.isNotEmpty() }?.let { docs ->
                    val totalRating = docs.sumOf { it.get("rating")?.toString()?.toIntOrNull() ?: 0 }
                    averageRating = (if (docs.isNotEmpty()) totalRating.toDouble() / docs.size else 0.0).coerceAtLeast(0.0)
                    Log.d("Firestore", "✅ Live Rating Updated: $averageRating")
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                artisan.artisanId?.let { id ->
                    Log.d("Navigation", "🚀 Navigating to ArtisanProfileScreen with ID: $id")
                    navController.navigate("artisanProfile/$id")
                } ?: Log.e("Navigation", "❌ Invalid artisanId detected!")
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // 💜 Change to any color you prefer!
    )  {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // ✅ Profile Picture Handling
                AsyncImage(
                    model = profilePicUrl,
                    contentDescription = "Artisan Profile Picture",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape),
                    error = painterResource(id = R.drawable.bd)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = artisan.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "🛠 Skills: ${artisan.skills}", fontSize = 16.sp)
                    Text(text = "📍 Location: ${artisan.location}", fontSize = 16.sp)
                    Text(text = "⭐ Rating: ${String.format("%.1f", averageRating)}", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔥 Display Top Reviews (Max 3)
            if (artisan.reviews.isNotEmpty()) {
                Text(text = "📝 Top Reviews:", fontWeight = FontWeight.Bold)
                artisan.reviews.take(3).forEach { review ->
                    Text("- ${review.comment} (⭐${review.rating})", fontStyle = FontStyle.Italic)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 🔥 Action Buttons (Chat & Request Service)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            )  {
            Button(
                onClick = {
                    artisan.artisanId?.let { id ->
                        Log.d("Navigation", "🚀 Navigating to ChatScreen with ID: $id")
                        navController.navigate("chat_screen/$id")
                    } ?: Log.e("Navigation", "❌ Attempted navigation with missing ID!")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF7043), // 💜 Purple Button Background
                    contentColor = Color.White // ✅ Text Color
                )
            ) {
                Text("💬 Chat")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    artisan.artisanId?.let { id ->
                        Log.d("Navigation", "🚀 Navigating to RequestFormScreen with ID: $id")
                        navController.navigate("request_form/$id")
                    } ?: Log.e("Navigation", "❌ Attempted navigation with missing ID!")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF7043), // 💜 Darker Purple Button Background
                    contentColor = Color.White // ✅ Text Color
                )
            ) {
                Text("📩 Request Service")
            }
        }
        }
    }
}