package com.example.juakaliconnect.screens

import androidx.navigation.NavHostController

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.juakaliconnect.data.ReviewRepository
import com.example.juakaliconnect.model.Artisan

@Composable
fun ArtisanSearchScreen(navController: NavHostController) {
    var skillQuery by remember { mutableStateOf("") }
    var artisans by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val reviewRepo = ReviewRepository()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = skillQuery,
            onValueChange = { skillQuery = it },
            label = { Text("Search skill...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (skillQuery.isNotBlank()) {
                    reviewRepo.getArtisansBySkill(skillQuery) { fetchedArtisans ->
                        artisans = fetchedArtisans
                    }
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Find Artisans")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(artisans.map { doc ->
                Artisan(
                    artisanId = doc["artisanId"]?.toString() ?: "",
                    fullName = doc["fullName"]?.toString() ?: "Unknown",
                    location = doc["location"]?.toString() ?: "Not provided",
                    skills = doc["skills"]?.toString() ?: "Not listed",
                    profilePic = doc["profilePic"]?.toString() ?: "",
                    averageRating = (doc["averageRating"] as? Number)?.toFloat() ?: 0.0f
                )
            }) { artisan ->
                ArtisanProfileCard(artisan, navController) // ✅ Now passing correct Artisan type
            }
        }
        }
    }
