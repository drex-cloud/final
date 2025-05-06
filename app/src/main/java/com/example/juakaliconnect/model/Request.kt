package com.example.juakaliconnect.model

data class Request(
    val requesterId: String,
    val requesterName: String,
    val description: String,
    val location: String
)