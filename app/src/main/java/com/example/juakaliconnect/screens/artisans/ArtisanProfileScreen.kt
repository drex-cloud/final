package com.example.juakaliconnect.screens.artisans

import android.util.Log
import com.example.juakaliconnect.R
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.juakaliconnect.screens.Reviews.ReviewSection
import com.example.juakaliconnect.screens.Reviews.fetchArtisanReviews
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanProfileScreen(navController: NavHostController, artisanId: String?) {
    val db = FirebaseFirestore.getInstance()
    val defaultProfilePic = "https://i.imgur.com/3fVBF3s.jpeg"
    val backgroundImageUrl = "https://i.imgur.com/YOUR_IMAGE_URL.jpeg" // ✅ Change to your image

    var artisanData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var currentUserRole by remember { mutableStateOf("") }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    if (artisanId.isNullOrEmpty()) {
        Log.e("Navigation", "❌ Invalid Artisan ID detected!")
        Text("⚠️ Error: Invalid Artisan ID", color = Color.Red)
        return
    }

    // 🔥 Fetch Artisan Profile
    LaunchedEffect(artisanId) {
        db.collection("users").document(artisanId).get()
            .addOnSuccessListener { document ->
                artisanData = document.data ?: emptyMap()
                Log.d("Firestore", "✅ Profile Data Fetched: $artisanData")
            }
            .addOnFailureListener { error ->
                Log.e("Firestore", "❌ Error fetching profile: ${error.message}")
            }
    }

    // 🔥 Fetch User Role
    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    currentUserRole = document.getString("role") ?: ""
                    Log.d("Firestore", "✅ User Role Fetched: $currentUserRole")
                }
                .addOnFailureListener { error ->
                    Log.e("Firestore", "❌ Error fetching user role: ${error.message}")
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Artisan Profile", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA) // 💙 Indigo Top Bar Color
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)) // ✅ Light Gray Background
        ) {
            Image(
                painter = rememberAsyncImagePainter(backgroundImageUrl),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                artisanData?.let { artisan ->
                    val profilePicUrl = artisan["profilePic"]?.toString()?.takeIf { it.isNotEmpty() } ?: defaultProfilePic

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)) // ✅ Soft Blue for Profile Card
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = profilePicUrl,
                                contentDescription = "Artisan Profile Picture",
                                modifier = Modifier.size(100.dp).clip(CircleShape).border(2.dp, Color.Gray, CircleShape),
                                error = painterResource(id = R.drawable.default_image)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("👤 ${artisan["fullName"] ?: "N/A"}", style = MaterialTheme.typography.titleLarge, color = Color.Black)
                            Text("📍 ${artisan["location"] ?: "N/A"}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF37474F)) // Dark gray text
                            Text("🛠 ${artisan["skills"] ?: "N/A"}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF37474F)) // Dark gray text
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔥 Reviews Section
                Text("Client Reviews", style = MaterialTheme.typography.titleMedium, color = Color.Black)

                Card(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // ✅ Light Gray for Reviews Card
                ) {
                    ReviewSection(artisanId)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 🔥 Navigate to `ReviewScreen.kt`
                if (currentUserRole == "Client") {
                    Button(
                        onClick = { navController.navigate("artisan_reviews/$artisanId") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF84148C), // ✅ Royal Purple Button
                            contentColor = Color.White
                        )
                    ) {
                        Text("🚀 Write a Review")
                    }
                }
            }
        }
    }
}