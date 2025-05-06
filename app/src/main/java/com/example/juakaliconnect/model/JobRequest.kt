package com.example.juakaliconnect.model

data class JobRequest(
    val jobId: String = "",
    val clientId: String = "",
    val title: String = "",
    val description: String = "",
    val budget: Int = 0,
    val status: String = "Pending",
    val applications: List<Application> = emptyList()
)
