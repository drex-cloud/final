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
                val reviews: List<Map<String, Any>> = document.get("reviews") as? List<Map<String, Any>> ?: emptyList() // ✅ Explicit type definition
                val parsedReviews = reviews.mapNotNull { map ->
                    Review(
                        artisanId = artisanId,
                        rating = map["rating"] as? Double ?: 0.0,
                        comment = map["comment"] as? String ?: ""
                    )
                }
                callback(parsedReviews)
            }
            .addOnFailureListener { e -> Log.e("Firestore", "❌ Error fetching reviews", e) }
    }

    fun calculateAverageRating(artisanId: String, callback: (Double) -> Unit) {
        val userRef = db.collection("users").document(artisanId)

        userRef.get().addOnSuccessListener { document ->
            val reviews: List<Map<String, Any>> = document.get("reviews") as? List<Map<String, Any>> ?: emptyList()
            val totalRating = reviews.mapNotNull { it["rating"] as? Double }.sum() // ✅ Fixed sumOf type inference issue
            val average = if (reviews.isNotEmpty()) totalRating / reviews.size else 0.0 // ✅ Fixed typo

            userRef.update("averageRating", average)
                .addOnFailureListener { e -> Log.e("Firestore", "❌ Error updating rating", e) }

            callback(average)
        }.addOnFailureListener { e ->
            Log.e("Firestore", "❌ Error calculating rating", e)
        }
    }

    fun getNearbyArtisans(userLat: Double, userLong: Double, maxDistance: Double, callback: (List<Map<String, Any>>) -> Unit) {
        val allArtisans = mutableListOf<Map<String, Any>>()

        db.collection("users").whereEqualTo("role", "artisan").get()
            .addOnSuccessListener { result ->
                for (document in result.documents) {
                    val location = document.get("location") as? Map<String, Any> ?: emptyMap()
                    val artisanLat = location["latitude"] as? Double ?: 0.0
                    val artisanLong = location["longitude"] as? Double ?: 0.0

                    val distance = calculateDistance(userLat, userLong, artisanLat, artisanLong)
                    if (distance <= maxDistance) {
                        document.data?.let { allArtisans.add(it) }
                    }
                }
                callback(allArtisans)
            }
            .addOnFailureListener { e -> Log.e("Firestore", "❌ Error fetching artisans", e) }
    }

    fun getFilteredArtisans(skill: String, minRating: Double, callback: (List<Map<String, Any>>) -> Unit) {
        db.collection("users")
            .whereEqualTo("role", "artisan")
            .whereArrayContains("skills", skill)
            .whereGreaterThanOrEqualTo("averageRating", minRating)
            .orderBy("averageRating", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val artisans = result.documents.mapNotNull { document ->
                    document.data?.filterValues { it != null } // ✅ Ensures no null values
                }
                callback(artisans)
            }
            .addOnFailureListener { e -> Log.e("Firestore", "❌ Error fetching filtered artisans", e) }
    }

    fun getArtisansBySkill(skill: String, callback: (List<Map<String, Any>>) -> Unit) {
        db.collection("users")
            .whereEqualTo("role", "artisan")
            .whereArrayContains("skills", skill)
            .orderBy("averageRating", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val artisans = result.documents.mapNotNull { document ->
                    document.data?.filterValues { it != null } // ✅ Filtering non-null values
                }
                callback(artisans)
            }
            .addOnFailureListener { e -> Log.e("Firestore", "❌ Error fetching artisans by skill", e) }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // 🌍 Radius of Earth in KM
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c // ✅ Returns distance in KM
    }
}