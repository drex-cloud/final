package com.example.juakaliconnect.model

data class Artisan(
    val artisanId: String = "",
    val fullName: String = "",
    val location: String = "",
    val skills: String = "",
    val profilePic: String = "",
    val averageRating: Float = 0.0f,
    val reviews: List<Review> = emptyList()
)