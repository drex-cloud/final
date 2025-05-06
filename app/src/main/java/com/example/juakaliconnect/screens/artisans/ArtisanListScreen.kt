package com.example.juakaliconnect.screens.artisans

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.juakaliconnect.data.ReviewRepository
import com.example.juakaliconnect.model.Artisan
import com.example.juakaliconnect.screens.ArtisanProfileCard
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async

@Composable
fun ArtisanListScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val artisans = remember { mutableStateListOf<Artisan>() }
    val reviewRepository = ReviewRepository()

    LaunchedEffect(Unit) {
        val artisanSet = mutableSetOf<Artisan>()

        // Fetch general artisans from Firestore
        db.collection("users").whereEqualTo("role", "Artisan").get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.e("Firestore", "No artisans found!")
                } else {
                    Log.d("Firestore", "Fetched ${documents.size()} artisans")
                    artisanSet.addAll(documents.mapNotNull { it.toObject(Artisan::class.java) }) // ✅ Converts data correctly
                    artisans.clear()
                    artisans.addAll(artisanSet) // ✅ Now stores as `List<Artisan>`
                }
            }
            .addOnFailureListener { error ->
                Log.e("Firestore", "Error fetching artisans: ${error.message}")
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Browse Artisans", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        if (artisans.isEmpty()) {
            Text("No artisans found.", color = Color.Red)
        } else {
            LazyColumn {
                items(artisans) { artisan ->
                    ArtisanProfileCard(artisan, navController) // ✅ Uses proper Artisan object
                }
            }
        }
    }
}