package com.example.juakaliconnect.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

fun sendMessage(context: Context, senderId: String, receiverId: String, text: String, imageUri: Uri? = null) {
    val db = FirebaseFirestore.getInstance()

    if (imageUri != null) {
        // Upload image first
        val uploadRepo = UploadRepository()
        uploadRepo.uploadToImgur(context, imageUri) { imageUrl ->
            val messageData = mapOf(
                "senderId" to senderId,
                "receiverId" to receiverId,
                "text" to text,
                "imageUrl" to (imageUrl ?: ""), // Store Imgur URL
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("messages").add(messageData)
                .addOnSuccessListener { Log.d("Chat", "✅ Message sent!") }
                .addOnFailureListener { error -> Log.e("Chat", "❌ Error sending message: ${error.message}") }
        }
    } else {
        // Send text-only message
        val messageData = mapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "text" to text,
            "imageUrl" to "",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("messages").add(messageData)
    }
}