package com.example.juakaliconnect.model

data class Artisan(
    val artisanId: String = "",
    val fullName: String = "",
    val skills: String = "",
    val location: String = "",
    val profilePicUrl: String = "", // ✅ Ensure this field is present
    val averageRating: Double = 0.0,
    val reviews: List<Review> = emptyList()
)