package com.example.juakaliconnect.model

data class ServiceRequest(
    val requestId: String = "",
    val clientId: String = "",
    val artisanId: String = "",
    val serviceDetails: String = "",
    val status: String = "Pending", // ✅ Default: Request starts as 'Pending'
    val timestamp: Long = System.currentTimeMillis()
)