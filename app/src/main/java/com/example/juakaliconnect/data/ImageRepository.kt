package com.example.juakaliconnect.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
fun saveImageUrlToFirestore(userId: String, imageUrl: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users").document(userId)
        .update("profileImage", imageUrl)
        .addOnSuccessListener { Log.d("Firestore", "Image URL saved successfully!") }
        .addOnFailureListener { Log.e("Firestore", "Failed to save image URL.") }
}



