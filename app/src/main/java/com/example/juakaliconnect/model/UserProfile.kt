package com.example.juakaliconnect.model

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val userType: String = "",
    val rating: Double = 0.0,
    val reviews: List<String> = emptyList()
)