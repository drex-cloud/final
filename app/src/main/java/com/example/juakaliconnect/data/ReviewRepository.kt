package com.example.juakaliconnect.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.juakaliconnect.model.Review
import com.google.firebase.firestore.Query


class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getArtisanReviews(artisanId: String, callback: (List<Review>) -> Unit) {
        val userRef = db.collection("users").document(artisanId)
        userRef.get()
            .addOnSuccessListener { document ->
                val reviews = document.get("reviews") as? List<Map<String, Any>> ?: emptyList()
                val parsedReviews = reviews.map {
                    Review(
                        artisanId = artisanId,
                        rating = it["rating"] as? Double ?: 0.0,
                        comment = it["comment"] as? String ?: ""
                    )
                }
                callback(parsedReviews)
            }
            .addOnFailureListener { e -> Log.e("Firestore", "Error fetching reviews", e) }
    }

    fun calculateAverageRating(artisanId: String, callback: (Double) -> Unit) {
        val userRef = db.collection("users").document(artisanId)

        userRef.get().addOnSuccessListener { document ->
            val reviews = document.get("reviews") as? List<Map<String, Any>> ?: emptyList()
            val totalRating = reviews.sumOf { it["rating"] as? Double ?: 0.0 }
            val average = if (reviews.isNotEmpty()) totalRating / reviews.size else 0.0

            userRef.update("averageRating", average) // 🔹 Save updated rating in Firestore
            callback(average)
        }
    }

    fun getTopRatedArtisans(callback: (List<Map<String, Any>>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("role", "artisan") // 🔹 Only fetch artisans
            .orderBy("averageRating", Query.Direction.DESCENDING) // 🔹 Highest rating first
            .get()
            .addOnSuccessListener { result ->
                val artisans = result.documents.map { it.data ?: emptyMap() }
                callback(artisans)
            }
            .addOnFailureListener { e -> Log.e("Firestore", "Error fetching artisans", e) }
    }

    fun getArtisansBySkill(skill: String, callback: (List<Map<String, Any>>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("role", "artisan")
            .whereArrayContains("skills", skill) // 🔹 Search artisans by skill
            .orderBy("averageRating", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val artisans = result.documents.map { it.data ?: emptyMap() }
                callback(artisans)
            }
            .addOnFailureListener { e -> Log.e("Firestore", "Error fetching artisans", e) }
    }
    fun getNearbyArtisans(userLat: Double, userLong: Double, maxDistance: Double, callback: (List<Map<String, Any>>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val allArtisans = mutableListOf<Map<String, Any>>()

        db.collection("users").whereEqualTo("role", "artisan").get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val location = document.get("location") as? Map<String, Double>
                    val artisanLat = location?.get("latitude") ?: 0.0
                    val artisanLong = location?.get("longitude") ?: 0.0

                    val distance = calculateDistance(userLat, userLong, artisanLat, artisanLong)
                    if (distance <= maxDistance) {
                        allArtisans.add(document.data!!)
                    }
                }
                callback(allArtisans)
            }
            .addOnFailureListener { e -> Log.e("Firestore", "Error fetching artisans", e) }
    }
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371 // Radius of Earth in KM
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c // 🔹 Distance in KM
    }
    fun getFilteredArtisans(skill: String, minRating: Double, callback: (List<Map<String, Any>>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("role", "artisan")
            .whereArrayContains("skills", skill) // 🔹 Search by skill
            .whereGreaterThanOrEqualTo("averageRating", minRating) // 🔹 Minimum rating filter
            .orderBy("averageRating", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val artisans = result.documents.map { it.data ?: emptyMap() }
                callback(artisans)
            }
            .addOnFailureListener { e -> Log.e("Firestore", "Error fetching filtered artisans", e) }
    }
}