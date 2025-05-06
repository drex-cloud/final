package com.example.juakaliconnect.screens.Reviews



import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

fun fetchArtisanReviews(artisanId: String, onResult: (List<Map<String, String>>) -> Unit) {
    FirebaseFirestore.getInstance().collection("users").document(artisanId).collection("reviews")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("ReviewFetch", "Failed to fetch reviews: ${error.message}")
                onResult(emptyList())
                return@addSnapshotListener
            }
            val reviews = snapshot?.documents?.map {
                mapOf(
                    "clientName" to (it["clientName"]?.toString() ?: "Anonymous"),
                    "comment" to (it["comment"]?.toString() ?: "")
                )
            } ?: emptyList()
            onResult(reviews)
        }
}
fun fetchMessages(senderId: String, receiverId: String, onResult: (List<Map<String, String>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("messages")
        .whereEqualTo("senderId", senderId)
        .whereEqualTo("receiverId", receiverId)
        .orderBy("timestamp", Query.Direction.ASCENDING)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Chat", "❌ Error fetching messages: ${error.message}")
                return@addSnapshotListener
            }
            val messages = snapshot?.documents?.map {
                mapOf(
                    "text" to it["text"]?.toString().orEmpty(),
                    "imageUrl" to it["imageUrl"]?.toString().orEmpty()
                )
            } ?: emptyList()
            onResult(messages)
        }
    db.collection("messages").get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d("Firestore", "Message: ${document.data}")
            }
        }
}
fun sendMessage(context: Context, senderId: String, receiverId: String, text: String, imageUri: Uri? = null) {
    val db = FirebaseFirestore.getInstance()

    val messageData = mapOf(
        "senderId" to senderId,
        "receiverId" to receiverId,
        "text" to text,
        "timestamp" to System.currentTimeMillis()
    )

    db.collection("messages").add(messageData)
}