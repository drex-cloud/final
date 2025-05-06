package com.example.juakaliconnect.screens.artisans

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MessagesScreen(navController: NavHostController) {
    val artisanId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var conversations by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }

    // 🔥 Fetch conversations dynamically
    LaunchedEffect(artisanId) {
        fetchConversations(artisanId) { fetchedConversations ->
            conversations = fetchedConversations
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("💬 Conversations", fontSize = 20.sp)

        if (conversations.isEmpty()) {
            Text("No messages yet.", color = Color.Gray)
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(conversations) { conversation ->
                ConversationCard(conversation, navController)
            }
        }
    }
}

// 🔥 Fetch Conversations for Artisans
fun fetchConversations(artisanId: String, onResult: (List<Map<String, String>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("messages")
        .whereEqualTo("receiverId", artisanId) // ✅ Only fetch messages sent to the artisan
        .orderBy("timestamp")
        .get()
        .addOnSuccessListener { snapshot ->
            val uniqueConversations = snapshot.documents.distinctBy { it.getString("senderId") }
                .map {
                    mapOf(
                        "requesterId" to it.getString("senderId").orEmpty(),
                        "requesterName" to it.getString("requesterName").orEmpty()
                    )
                }

            Log.d("Firestore", "✅ Conversations loaded: $uniqueConversations")
            onResult(uniqueConversations)
        }
        .addOnFailureListener { error ->
            Log.e("Firestore", "❌ Failed to fetch conversations: ${error.message}")
        }
}