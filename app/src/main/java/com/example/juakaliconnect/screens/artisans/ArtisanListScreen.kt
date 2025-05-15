package com.example.juakaliconnect.screens.artisans

import com.example.juakaliconnect.R
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.juakaliconnect.auth.AuthViewModel
import com.example.juakaliconnect.navigation.BottomNavBar
import com.example.juakaliconnect.model.Artisan
import com.example.juakaliconnect.screens.bars.CustomTopAppBarWithSearch
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtisanListScreen(navController: NavHostController, authViewModel: AuthViewModel, userRole: String) {

    val db = FirebaseFirestore.getInstance()
    val artisans = remember { mutableStateListOf<Artisan>() }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) } // 🔄 Toggle search visibility

    // 🔥 Real-time artisan fetching
    LaunchedEffect(Unit) {
        db.collection("users")
            .whereEqualTo("role", "Artisan")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("Firestore", "❌ Error fetching artisans: ${error.message}")
                    return@addSnapshotListener
                }
                snapshots?.let {
                    val artisanSet = snapshots.documents.mapNotNull { doc ->
                        Artisan(
                            artisanId = doc.id,
                            fullName = doc.getString("fullName") ?: "Unknown",
                            location = doc.getString("location") ?: "Not provided",
                            skills = doc.getString("skills") ?: "Not listed",
                            profilePicUrl = doc.getString("profilePic") ?: "",
                            averageRating = (doc.getDouble("averageRating") ?: 0.0)
                        )
                    }
                    artisans.clear()
                    artisans.addAll(artisanSet)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Welcome, Client!", fontWeight = FontWeight.Bold, color = Color.White)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color(0xFF2196F3),
                    actionIconContentColor = Color(0xFF1976D2)
                ),
                actions = {
                    IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, authViewModel, userRole) } // ✅ Logout added via BottomNavBar
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bc),
                contentDescription = "Artisan List Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                if (isSearchVisible) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search artisans") },
                        colors = TextFieldDefaults.colors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val filteredArtisans = artisans.filter { artisan ->
                    artisan.skills.contains(searchQuery, ignoreCase = true) ||
                            artisan.location.contains(searchQuery, ignoreCase = true)
                }

                if (filteredArtisans.isEmpty()) {
                    Text("No artisans found.", color = Color.Red)
                } else {
                    LazyColumn {
                        items(filteredArtisans) { artisan ->
                            ArtisanProfileCard(artisan, navController)
                        }
                    }
                }
            }
        }
    }
}