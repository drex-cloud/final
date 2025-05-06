package com.example.juakaliconnect.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController, receiverId: String) {
    val context = LocalContext.current
    val senderId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val db = FirebaseFirestore.getInstance()

    var messages by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var messageText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Fetch messages dynamically
    LaunchedEffect(senderId, receiverId) {
        if (senderId.isNotEmpty() && receiverId.isNotEmpty()) {
            fetchMessages(senderId, receiverId) { fetchedMessages ->
                messages = fetchedMessages
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Chat", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {

            // Messages List
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages) { message ->
                    val isSentByUser = message["senderId"] == senderId

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                                .background(if (isSentByUser) Color.Blue else Color.Gray, RoundedCornerShape(8.dp))
                        ) {
                            Text(message["text"].orEmpty(), color = Color.White)

                            message["imageUrl"]?.takeIf { it.isNotEmpty() }?.let { imageUrl ->
                                AsyncImage(model = imageUrl, contentDescription = "Sent Image")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Image Picker
            ImagePicker { selectedUri -> imageUri = selectedUri }

            Spacer(modifier = Modifier.height(8.dp))

            // Message Input & Send Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") }
                )

                Button(onClick = {
                    if (messageText.isNotBlank() || imageUri != null) {
                        sendMessage(context, senderId, receiverId, messageText, imageUri)
                        messageText = ""
                        imageUri = null

                        // 🔥 Fetch messages again after sending
                        fetchMessages(senderId, receiverId) { fetchedMessages ->
                            messages = fetchedMessages
                        }
                    } else {
                        Toast.makeText(context, "Enter text or select an image", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Send")
                }
            }
        }
    }
}

// 🔥 Fetch Messages Function
fun fetchMessages(senderId: String, receiverId: String, onResult: (List<Map<String, String>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("messages")
        .whereIn("senderId", listOf(senderId, receiverId))
        .whereIn("receiverId", listOf(senderId, receiverId))
        .orderBy("timestamp")
        .get()
        .addOnSuccessListener { snapshot ->
            Log.d("Firestore", "✅ Messages fetched: ${snapshot.documents.size}") // ✅ Debug message count

            val messages = snapshot.documents.map {
                mapOf(
                    "senderId" to it.getString("senderId").orEmpty(),
                    "receiverId" to it.getString("receiverId").orEmpty(),
                    "text" to it.getString("text").orEmpty(),
                    "imageUrl" to it.getString("imageUrl").orEmpty()
                )
            }

            Log.d("Firestore", "🔥 Retrieved Messages: $messages") // ✅ Full message details
            onResult(messages)
        }
        .addOnFailureListener { error ->
            Log.e("Firestore", "❌ Failed to fetch messages: ${error.message}")
        }
}

// 🔥 Send Message Function
fun sendMessage(context: android.content.Context, senderId: String, receiverId: String, text: String, imageUri: Uri? = null) {
    val db = FirebaseFirestore.getInstance()

    val messageData = mutableMapOf(
        "senderId" to senderId,
        "receiverId" to receiverId,
        "text" to text,
        "timestamp" to System.currentTimeMillis().toString()
    )

    imageUri?.let {
        messageData["imageUrl"] = it.toString()
    }

    db.collection("messages").add(messageData)
        .addOnSuccessListener {
            Log.d("Firestore", "✅ Message sent successfully!")
        }
        .addOnFailureListener { error ->
            Log.e("Firestore", "❌ Failed to send message: ${error.message}")
        }
}

// 🔥 Image Picker Component
@Composable
fun ImagePicker(onImageSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onImageSelected(it) }
    }

    Button(onClick = { launcher.launch("image/*") }) {
        Text("📸 Pick Image")
    }
}