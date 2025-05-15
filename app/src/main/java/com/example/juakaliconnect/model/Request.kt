package com.example.juakaliconnect.model

data class Request(
    val id: String = "",
    val requesterId: String,
    val requesterName: String,
    val description: String,
    val location: String,
    val status: String = "pending" // New field to track acceptance
)
