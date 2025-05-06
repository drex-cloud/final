package com.example.juakaliconnect.data


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    fun saveUserProfile(userId: String, name: String, email: String, userType: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "user_type" to userType
        )
        db.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "User profile added successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding profile", e)
            }
    }

    fun getUserProfile(userId: String, callback: (DocumentSnapshot?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                callback(document)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error retrieving profile", e)
            }
    }
}